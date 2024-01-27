package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.*;
import com.lsh.mavikarga.repository.CartRepository;
import com.lsh.mavikarga.repository.OrderRepository;
import com.lsh.mavikarga.repository.PaymentRepository;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * 결재 검증
     * @param irsp: 포트원쪽에서 결재 정보
     * @param paid_amount: 실제 유저 결재 금액
     */
    public boolean validatePayment(IamportResponse<Payment> irsp, int paid_amount, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if(user == null) {
            return false;
        }

        // 포트원 서버에서 조회된 결재금액과 실제 사용자 결재 금액이 다름
        // getAmount() 결과는 BigDecimal
        if (irsp.getResponse().getAmount().intValue() != paid_amount) {
            return false;
        }

        //  DB에 저장된 물품의 실제금액과 비교
        int priceToPay = calPriceToPay(user);
        if(priceToPay != paid_amount) {
            return false;
        }

        // 검증 완료 -> DB에 저장
        log.info("irsp.getResponse().getAmount().intValue() = {}", irsp.getResponse().getAmount().intValue());
        log.info("amount = {}", paid_amount);

        // 주문정보 생성
        PaymentInfo paymentInfo = new PaymentInfo(
                irsp.getResponse().getPayMethod(),
                irsp.getResponse().getImpUid(),
                irsp.getResponse().getMerchantUid(),
                irsp.getResponse().getAmount().intValue(),
                irsp.getResponse().getBuyerAddr(),
                irsp.getResponse().getBuyerPostcode(),
                LocalDateTime.now()
        );

        // 주문제품 생성
        List<OrderProduct> orderProductList = new ArrayList<>();
        for (Cart cart : user.getCarts()) {
            ProductSize productSize = cart.getProductSize();
            OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, productSize.getProduct().getPrice(), cart.getCount());
            orderProductList.add(orderProduct);
        }
        // 주문 생성
        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProductList, paymentInfo);
        // 주문 저장
        orderRepository.save(orderInfo);

        // PaymentInfo 저장
        paymentRepository.save(paymentInfo);

        // 장바구니에 있는 물품들 결재 완료됐으니 장바구니 비운다
        for (Cart cart : user.getCarts()) {
            cartRepository.delete(cart);
        }
        user.getCarts().clear();

        return true;
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


//    /**
//     * 결제 취소할때 필요한 파라미터들을
//     * CancelData에 셋업해주고 반환함.
//     * @param map
//     * @param impUid
//     * @param bankAccount
//     * @param code
//     * @return
//     * @throws RefundAmountIsDifferent
//     */
//    @Transactional
//    public CancelData cancelData(Map<String,String> map,
//                                 IamportResponse<Payment> lookUp,
//                                 PrincipalDetail principal, String code) throws RefundAmountIsDifferent {
//        //아임포트 서버에서 조회된 결제금액 != 환불(취소)될 금액 이면 예외발생
//        if(lookUp.getResponse().getAmount()!=new BigDecimal(map.get("checksum")))
//            throw new RefundAmountIsDifferent();
//
//        CancelData data = new CancelData(lookUp.getResponse().getImpUid(),true);
//        data.setReason(map.get("reason"));
//        data.setChecksum(new BigDecimal(map.get("checksum")));
//        data.setRefund_holder(map.get("refundHolder"));
//        data.setRefund_bank(code);
//        data.setRefund_account(principal.getBankName());
//        return data;
//    }
}
