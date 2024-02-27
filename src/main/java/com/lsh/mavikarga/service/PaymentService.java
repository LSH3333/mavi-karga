package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.*;
import com.lsh.mavikarga.dto.PaymentRequestDto;
import com.lsh.mavikarga.repository.CartRepository;
import com.lsh.mavikarga.repository.DeliveryRepository;
import com.lsh.mavikarga.repository.OrderRepository;
import com.lsh.mavikarga.repository.PaymentRepository;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.servlet.http.HttpSession;
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
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserService userService, OrderRepository orderRepository, CartRepository cartRepository,
                          DeliveryRepository deliveryRepository) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.deliveryRepository = deliveryRepository;
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
    public String validatePayment(IamportResponse<Payment> irsp, PaymentRequestDto paymentRequestDto, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return null;
        }

        int paid_amount = Integer.parseInt(paymentRequestDto.getPaid_amount());
        // 포트원 서버에서 조회된 결재금액과 실제 사용자 결재 금액이 다름
        // getAmount() 결과는 BigDecimal
        if (irsp.getResponse().getAmount().intValue() != paid_amount) {
            return null;
        }

        //  DB에 저장된 물품의 실제금액과 비교
        int priceToPay = calPriceToPay(user);
        if (priceToPay != paid_amount) {
            return null;
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
            ProductOption productSize = cart.getProductOption();
            OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, productSize.getProduct().getPrice(), cart.getCount());
            orderProductList.add(orderProduct);
        }
        // 주문정보 생성
        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProductList, paymentInfo, delivery, generateOrderInfoLookUpNumber(), paymentInfo.getMerchantUid());

        // 주문정보 저장
        orderRepository.save(orderInfo);

        // 장바구니에 있는 물품들 결재 완료됐으니 장바구니 비운다
        clearCart(user);

        return orderInfo.getOrderLookUpNumber();
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
            int price = cart.getProductOption().getProduct().getPrice();
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


    ////////////////////////// 비회원 //////////////////////////
    public String validatePaymentNonUser(IamportResponse<Payment> irsp, PaymentRequestDto paymentRequestDto, HttpSession session) {

        // 세션에서 장바구니 가져옴
        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");


        int portOnePaidAmount = irsp.getResponse().getAmount().intValue(); // 포트원 서버에서 조회한 실제 결제 금액

        //  DB에 저장된 물품의 실제금액과 비교
        int priceToPay = calPriceToPayNonUser(cartList);
        if (priceToPay != portOnePaidAmount) {
            return null;
        }

        // 검증 완료 -> DB에 저장
        log.info("irsp.getResponse().getAmount().intValue() = {}", irsp.getResponse().getAmount().intValue());
        log.info("amount = {}", portOnePaidAmount);

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
        for (CartForNonUser cartForNonUser : cartList) {
            ProductOption productSize = cartForNonUser.getProductSize();
            OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, productSize.getProduct().getPrice(), cartForNonUser.getCount());
            orderProductList.add(orderProduct);
        }

        // 주문정보 생성
        OrderInfo orderInfo = OrderInfo.createOrderInfo(null, orderProductList, paymentInfo, delivery, generateOrderInfoLookUpNumber(), paymentInfo.getMerchantUid());

        // 주문정보 저장
        orderRepository.save(orderInfo);

        // 장바구니에 있는 물품들 결재 완료됐으니 장바구니 비운다
        session.removeAttribute("cart");

        return orderInfo.getOrderLookUpNumber();
    }

    private int calPriceToPayNonUser(List<CartForNonUser> cartList) {
        int priceToPay = 0;
        for (CartForNonUser cartForNonUser : cartList) {
            int price = cartForNonUser.getProductSize().getProduct().getPrice();
            int count = cartForNonUser.getCount();
            priceToPay += (price * count);
        }
        return priceToPay;
    }

    // 주문 조회 번호 생성
    // UUID 에서 '-' 제외한 문자열
    private String generateOrderInfoLookUpNumber() {
        UUID uuid = UUID.randomUUID();
        String uuid_string = uuid.toString().replaceAll("-", "");
        return uuid_string;
    }

    //////////////////////////////////////
    // order 정보 저장
    public void storeOrder(PaymentRequestDto paymentRequestDto, HttpSession session) {
        log.info("storeOrder");
        // 세션에서 장바구니 가져옴
        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");

        // 결제정보 생성
        PaymentInfo paymentInfo = new PaymentInfo(
                "",
                "",
                paymentRequestDto.getMerchant_uid(),
                0,
                "",
                "",
                LocalDateTime.now()
        );

        // 배송정보 생성
        Delivery delivery = new Delivery(paymentRequestDto.getName(), paymentRequestDto.getEmail(), paymentRequestDto.getPhone(),
                paymentRequestDto.getPostcode(), paymentRequestDto.getRoadAddress(), paymentRequestDto.getJibunAddress(), paymentRequestDto.getDetailAddress(),
                paymentRequestDto.getExtraAddress());

        // 주문제품 생성
        List<OrderProduct> orderProductList = new ArrayList<>();
        for (CartForNonUser cartForNonUser : cartList) {
            ProductOption productSize = cartForNonUser.getProductSize();
            OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, productSize.getProduct().getPrice(), cartForNonUser.getCount());
            orderProductList.add(orderProduct);
        }

        // 주문정보 생성
        OrderInfo orderInfo = OrderInfo.createOrderInfo(null, orderProductList, paymentInfo, delivery, generateOrderInfoLookUpNumber(), paymentInfo.getMerchantUid());

        // 주문정보 저장
        orderRepository.save(orderInfo);

    }

    // 포트원 웹훅, 결제 정보 검증
    public String validateWebHook(IamportResponse<Payment> irsp) {
        // 세션에서 장바구니 가져옴
//        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");

        int portOnePaidAmount = irsp.getResponse().getAmount().intValue(); // 포트원 서버에서 조회한 실제 결제 금액
        log.info("포트원 결제 금액 = {}", portOnePaidAmount);
        //  DB에 저장된 물품의 실제금액과 비교
        int priceToPay = 0;
        PaymentInfo paymentInfo = paymentRepository.findByMerchantUid(irsp.getResponse().getMerchantUid());
        OrderInfo orderInfo = paymentInfo.getOrderInfo();
        List<OrderProduct> orderProductList = orderInfo.getOrderProducts();
        for (OrderProduct orderProduct : orderProductList) {
            priceToPay += orderProduct.getTotalPrice();
        }
        log.info("결제되어야 하는 금액 = {}", priceToPay);

        // 검증 실패
        if(priceToPay != portOnePaidAmount) {
            log.info("검증 실패");
            // db에 저장해놓은 OrderInfo 삭제 (연관된 것들도 모두 삭제)
            orderRepository.delete(orderInfo);
            return null;
        }

        log.info("검증 성공");
        paymentInfo.setInfos(
                irsp.getResponse().getPayMethod(),
                irsp.getResponse().getImpUid(),
                irsp.getResponse().getMerchantUid(),
                irsp.getResponse().getAmount().intValue(),
                irsp.getResponse().getBuyerAddr(),
                irsp.getResponse().getBuyerPostcode(),
                LocalDateTime.now()
        );


        return orderInfo.getOrderLookUpNumber();
    }
}
