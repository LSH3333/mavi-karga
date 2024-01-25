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

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserService userService, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    /**
     *
     * @param irsp: 포트원쪽에서 결재 정보
     * @param paid_amount: 실제 유저 결재 금액
     */
    public boolean validatePayment(IamportResponse<Payment> irsp, int paid_amount, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);

        // 포트원 서버에서 조회된 결재금액과 실제 사용자 결재 금액이 다름
        // getAmount() 결과는 BigDecimal
        if (irsp.getResponse().getAmount().intValue() != paid_amount) {
            return false;
        }

        //  DB에 저장된 물품의 실제금액과 비교
        int priceToPay = calPriceToPay(user);
        log.info("priceToPay = {}", priceToPay);
        if(priceToPay != paid_amount) {
            return false;
        }

        // 검증 완료 -> DB에 저장
        log.info("irsp.getResponse().getAmount().intValue() = {}", irsp.getResponse().getAmount().intValue());
        log.info("amount = {}", paid_amount);

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



        // todo: 장바구니 비우기

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
}
