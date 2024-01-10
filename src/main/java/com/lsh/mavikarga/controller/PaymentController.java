package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.dto.PaymentRequestDto;
import com.siot.IamportRestClient.IamportClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Slf4j
public class PaymentController {

    // https://github.com/iamport/iamport-rest-client-java
    private final IamportClient iamportClientApi;

    public PaymentController() {
        this.iamportClientApi = new IamportClient("0053241158344250",
                "VgsaKTATZ9bt9LDbE4snPbPe1uz9TQ7ls08cn6dIabsTp53auvZgqJNa0qk5rDq6pFSjGWo1MzLYDpgv");
    }

    // 결재 검증
    @PostMapping("/payment/validate")
    private ResponseEntity<String> preValidate(@ModelAttribute PaymentRequestDto paymentRequestDto) {
        log.info("============= /payment/validate");
        log.info("paymentRequestDto = {}", paymentRequestDto);
        String impUid = paymentRequestDto.getImp_uid(); // 결재 고유번호
        Long amount = Long.parseLong(paymentRequestDto.getAmount());  // 실제로 유저가 결제한 금액
        String merchant_uid = paymentRequestDto.getMerchant_uid();

        log.info("merchant_uid = {}", merchant_uid);
        log.info("impUid = {}", impUid);
        log.info("amount = {}", amount);

        return ResponseEntity.status(HttpStatus.OK).body("결재 정보 검증");
    }
}
