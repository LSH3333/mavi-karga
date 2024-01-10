package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.dto.PaymentRequestDto;
import com.lsh.mavikarga.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
@Slf4j
public class PaymentController {

    // https://github.com/iamport/iamport-rest-client-java
    private final IamportClient iamportClientApi;
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.iamportClientApi = new IamportClient("0053241158344250",
                "VgsaKTATZ9bt9LDbE4snPbPe1uz9TQ7ls08cn6dIabsTp53auvZgqJNa0qk5rDq6pFSjGWo1MzLYDpgv");
        this.paymentService = paymentService;
    }


    /**
     * impUid 로 결재내역 조회
     * @param impUid
     * @return
     * @throws IamportResponseException
     * @throws IOException
     */
    private IamportResponse<Payment> paymentLookup(String impUid) throws IamportResponseException, IOException {
        return iamportClientApi.paymentByImpUid(impUid);
    }

    // 클라이언트에서 결재요청 성공 후 받는 end point
    @PostMapping("/payment/validate")
    private ResponseEntity<String> validatePayment(@ModelAttribute PaymentRequestDto paymentRequestDto)
            throws IamportResponseException, IOException {
        log.info("============= /payment/validate");
        log.info("paymentRequestDto = {}", paymentRequestDto);

        String impUid = paymentRequestDto.getImp_uid(); // 결재 고유번호
        int amount = Integer.parseInt(paymentRequestDto.getPaid_amount());  // 실제로 유저가 결제한 금액
        String merchant_uid = paymentRequestDto.getMerchant_uid();

        IamportResponse<Payment> irsp = paymentLookup(impUid);
        paymentService.validatePayment(irsp, amount);

        return ResponseEntity.status(HttpStatus.OK).body("결재 정보 검증");
    }
}
