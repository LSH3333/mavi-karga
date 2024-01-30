package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.*;
import com.lsh.mavikarga.dto.PaymentRequestDto;
import com.lsh.mavikarga.repository.CartRepository;
import com.lsh.mavikarga.repository.OrderRepository;
import com.lsh.mavikarga.repository.PaymentRepository;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserService userService, OrderRepository orderRepository, CartRepository cartRepository) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * 은행이름에 따른 코드들을 반환해줌
     * KG이니시스 기준.
     *
     * @param bankName
     * @return
     */
    public String code(String bankName) {
        String code = switch (bankName) {
            case "국민은행", "국민" -> "04";
            case "제일은행", "제일" -> "23";
            case "경남은행", "경남" -> "39";
            case "광주은행", "광주" -> "34";
            case "기업은행", "기업" -> "03";
            case "농협은행", "농협" -> "11";
            case "대구은행", "대구" -> "31";
            case "부산은행", "부산" -> "32";
            case "산업은행", "산업" -> "02";
            case "새마을금고", "새마을" -> "45";
            case "수협은행", "수협" -> "07";
            case "신한은행", "신한" -> "88";
            case "신협은행", "신협" -> "48";
            case "하나은행", "하나", "외한은행" -> "81";
            case "우리은행", "우리" -> "20";
            case "우채국" -> "71";
            case "전북은행", "전북" -> "37";
            case "축협은행", "축협" -> "12";
            case "카카오뱅크", "카카오" -> "90";
            case "케이뱅크", "케이" -> "89";
            case "한국씨티은행", "한국씨티" -> "27";
            case "토스뱅크", "토스" -> "92";
            default -> "";
        };

        return code;
    }

    /**
     * 결재 검증 후 성공적이라면 주문정보, 결제정보, 배송정보 생성
     *
     * @param irsp:        포트원쪽에서 결재 정보
     * @param paymentRequestDto: 결재 페이지에서 사용자에게 입력 받은 정보들 (이름,이메일,배송정보 등)
     */
    public boolean validatePayment(IamportResponse<Payment> irsp, PaymentRequestDto paymentRequestDto, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return false;
        }

        int paid_amount = Integer.parseInt(paymentRequestDto.getPaid_amount());
        // 포트원 서버에서 조회된 결재금액과 실제 사용자 결재 금액이 다름
        // getAmount() 결과는 BigDecimal
        if (irsp.getResponse().getAmount().intValue() != paid_amount) {
            return false;
        }

        //  DB에 저장된 물품의 실제금액과 비교
        int priceToPay = calPriceToPay(user);
        if (priceToPay != paid_amount) {
            return false;
        }

        // 검증 완료 -> DB에 저장
        log.info("irsp.getResponse().getAmount().intValue() = {}", irsp.getResponse().getAmount().intValue());
        log.info("amount = {}", paid_amount);

        // 결제정보 생성
        PaymentInfo paymentInfo = new PaymentInfo(
                irsp.getResponse().getPayMethod(),
                irsp.getResponse().getImpUid(),
                irsp.getResponse().getMerchantUid(),
                irsp.getResponse().getAmount().intValue(),
                irsp.getResponse().getBuyerAddr(),
                irsp.getResponse().getBuyerPostcode(),
                LocalDateTime.now()
        );

        // 배송정보 생성
        Delivery delivery = new Delivery(paymentRequestDto.getName(), paymentRequestDto.getEmail(), paymentRequestDto.getPhone(),
                paymentRequestDto.getPostcode(), paymentRequestDto.getRoadAddress(), paymentRequestDto.getJibunAddress(), paymentRequestDto.getDetailAddress(),
                paymentRequestDto.getExtraAddress());

        // 주문제품 생성
        List<OrderProduct> orderProductList = new ArrayList<>();
        for (Cart cart : user.getCarts()) {
            ProductSize productSize = cart.getProductSize();
            OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, productSize.getProduct().getPrice(), cart.getCount());
            orderProductList.add(orderProduct);
        }
        // 주문정보 생성
        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProductList, paymentInfo, delivery);

        // 주문정보 저장
        orderRepository.save(orderInfo);

        // 장바구니에 있는 물품들 결재 완료됐으니 장바구니 비운다
        clearCart(user);

        return true;
    }

    // 사용자의 장바구니 비움
    private void clearCart(User user) {
        for (Cart cart : user.getCarts()) {
            cartRepository.delete(cart);
        }
        user.getCarts().clear();
    }

    private int calPriceToPay(User user) {
//        User user = userService.findByUsername(principal.getName()).orElse(null);
        List<Cart> carts = user.getCarts();
        int priceToPay = 0;
        for (Cart cart : carts) {
            int price = cart.getProductSize().getProduct().getPrice();
            int count = cart.getCount();
            priceToPay += (price * count);
        }
        return priceToPay;
    }


    /**
     * 결제 취소할때 필요한 파라미터들을
     * CancelData에 셋업해주고 반환함.
     */
    @Transactional
    public CancelData cancelData(Map<String,String> map,
                                 IamportResponse<Payment> lookUp, String code) throws IOException {

        //아임포트 서버에서 조회된 결제금액 != 환불(취소)될 금액 이면 예외발생
        log.info("CANCELDATA");
        log.info("lookUp.getResponse().getAmount() = {}", lookUp.getResponse().getAmount());
        log.info("checksum = {}", map.get("checksum"));
        log.info("Big Integer = {}", new BigDecimal(map.get("checksum")));

        if (new BigDecimal(map.get("checksum")).compareTo(lookUp.getResponse().getAmount()) != 0) {
            log.info("NOT SAME");
            throw new IOException();
        }

        CancelData data = new CancelData(lookUp.getResponse().getImpUid(),true);
        data.setReason(map.get("reason"));
        data.setChecksum(new BigDecimal(map.get("checksum")));
        data.setRefund_holder(map.get("refundHolder"));
        data.setRefund_bank(code);
        data.setRefund_account(map.get("refund_bank"));
        return data;
    }
}
