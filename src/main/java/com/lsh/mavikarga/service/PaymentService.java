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






    // 결제 취소할때 필요한 파라미터들을 CancelData에 셋업해주고 반환함.
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

    // 결제 취소되었을때 호출됨
    // 저장되어 있는 사용자 배송 정보들 삭제함
    public boolean deleteStoredUserInfo(String merchant_uid) {
        PaymentInfo paymentInfo = paymentRepository.findByMerchantUid(merchant_uid);
        if(paymentInfo == null) return false;
        OrderInfo orderInfo = paymentInfo.getOrderInfo();
        // db에 저장해놓은 OrderInfo 삭제 (연관된 것들도 모두 삭제)
        orderRepository.delete(orderInfo);
        return true;
    }

    ////////////////////////// 비회원 //////////////////////////



    // 주문 조회 번호 생성
    // UUID 에서 '-' 제외한 문자열
    private String generateOrderInfoLookUpNumber() {
        UUID uuid = UUID.randomUUID();
        String uuid_string = uuid.toString().replaceAll("-", "");
        return uuid_string;
    }

    //////////////////////// 결제창에서 유저가 입력한 배송 정보들 저장 ////////////////////////
    public String storeOrder(PaymentRequestDto paymentRequestDto, HttpSession session, Principal principal) {
        log.info("storeOrder");
        if(principal == null) {
            return storeOrderNonUser(paymentRequestDto, session);
        } else {
            return storeOrderUser(paymentRequestDto, principal);
        }
    }

    // order 정보 저장
    public String storeOrderNonUser(PaymentRequestDto paymentRequestDto, HttpSession session) {
        // 세션에서 장바구니 가져옴
        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");
        log.info("storeOrderNonUser merchant_uid = {}", paymentRequestDto.getMerchant_uid());

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
        String orderLookUpNumber = generateOrderInfoLookUpNumber();
        log.info("orderLookUpNumber = {}", orderLookUpNumber);

        OrderInfo orderInfo = OrderInfo.createOrderInfo(null, orderProductList, paymentInfo, delivery, orderLookUpNumber, paymentInfo.getMerchantUid());

        // 주문정보 저장
        orderRepository.save(orderInfo);

        return orderInfo.getOrderLookUpNumber();
    }

    public String storeOrderUser(PaymentRequestDto paymentRequestDto, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return null;
        }
        log.info("storeOrderUser merchant_uid = {}", paymentRequestDto.getMerchant_uid());

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
        for (Cart cart : user.getCarts()) {
            ProductOption productSize = cart.getProductOption();
            OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, productSize.getProduct().getPrice(), cart.getCount());
            orderProductList.add(orderProduct);
        }
        // 주문정보 생성
        String orderLookUpNumber = generateOrderInfoLookUpNumber();
        log.info("orderLookUpNumber = {}", orderLookUpNumber);
        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProductList, paymentInfo, delivery, orderLookUpNumber, paymentInfo.getMerchantUid());

        // 주문정보 저장
        orderRepository.save(orderInfo);


        return orderInfo.getOrderLookUpNumber();
    }

    // 포트원 웹훅, 결제 정보 검증
    public String validateWebHook(IamportResponse<Payment> irsp, Principal principal, HttpSession session) {
        // 세션에서 장바구니 가져옴
//        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");

        int portOnePaidAmount = irsp.getResponse().getAmount().intValue(); // 포트원 서버에서 조회한 실제 결제 금액
        log.info("포트원 결제 금액 = {}", portOnePaidAmount);
        log.info("merchant uid = {}", irsp.getResponse().getMerchantUid());
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

        // 장바구니 비우기
        if(principal != null) {
            // 회원 장바구니 비우기
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if(user != null) {
                clearUserCart(user);
            }
        } else {
            // 비회원 장바구니 제거
            session.removeAttribute("cart");
        }

        log.info("orderInfo.getOrderLookUpNumber() = {}", orderInfo.getOrderLookUpNumber());
        return orderInfo.getOrderLookUpNumber();
    }


    // 사용자의 장바구니 비움
    private void clearUserCart(User user) {
        for (Cart cart : user.getCarts()) {
            cartRepository.delete(cart);
        }
        user.getCarts().clear();
    }

}
