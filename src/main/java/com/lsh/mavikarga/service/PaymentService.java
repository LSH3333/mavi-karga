package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.Cart;
import com.lsh.mavikarga.domain.PaymentInfo;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.repository.PaymentRepository;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    /**
     *
     * @param irsp: 포트원쪽에서 결재 정보
     * @param paid_amount: 실제 유저 결재 금액
     */
    public boolean validatePayment(IamportResponse<Payment> irsp, int paid_amount, Principal principal) {

        // 포트원 서버에서 조회된 결재금액과 실제 사용자 결재 금액이 다름
        // getAmount() 결과는 BigDecimal
        if (irsp.getResponse().getAmount().intValue() != paid_amount) {
            return false;
        }

        //  DB에 저장된 물품의 실제금액과 비교
        int priceToPay = calPriceToPay(principal);
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

        paymentRepository.save(paymentInfo);
        // todo: OrderInfo 생성
        

        return true;
    }

    private int calPriceToPay(Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
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
