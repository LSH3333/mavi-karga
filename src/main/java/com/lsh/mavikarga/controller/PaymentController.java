package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.CartProductDtoList;
import com.lsh.mavikarga.dto.PaymentRequestDto;
import com.lsh.mavikarga.service.OrderService;
import com.lsh.mavikarga.service.PaymentService;
import com.lsh.mavikarga.service.UserService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
@Slf4j
public class PaymentController {

    // https://github.com/iamport/iamport-rest-client-java
    private final IamportClient iamportClientApi;
    private final PaymentService paymentService;

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public PaymentController(PaymentService paymentService, UserService userService, OrderService orderService) {
        this.iamportClientApi = new IamportClient("0053241158344250",
                "VgsaKTATZ9bt9LDbE4snPbPe1uz9TQ7ls08cn6dIabsTp53auvZgqJNa0qk5rDq6pFSjGWo1MzLYDpgv");
        this.paymentService = paymentService;

        this.userService = userService;
        this.orderService = orderService;
    }

    // 테스트용
    @GetMapping("/payTest")
    public String payTest() {
        return "payments/payTest";
    }

    // 결재 성공 폼
    @GetMapping("/payments/paymentSuccess")
    public String paymentSuccessForm() {
        return "/payments/paymentSuccess";
    }

    // 결재 실패 폼
    @GetMapping("/payments/paymentFail")
    public String paymentFailForm() {
        return "/payments/paymentFail";
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

    // 결재 페이지
    @GetMapping("/payments/payment")
    public String paymentForm(Principal principal, Model model) {
        log.info("PAYMENT FORM");
        User user = userService.findByUsername(principal.getName()).orElse(null);

        // 사용자 장바구니 담긴 상품들 보여주기
        CartProductDtoList cartProductDtoList = CartProductDtoList.createForPaymentForm(orderService.createCartProductDtoList(user.getId()));
//        log.info("forPaymentForm = {}", cartProductDtoList.getTotalAmount());
        model.addAttribute("cartProductDtoList", cartProductDtoList);

        return "payments/payment";
    }

    // 클라이언트에서 결재요청 성공 후 받는 end point
    @PostMapping("/payments/validate")
    private ResponseEntity<String> validatePayment(@ModelAttribute PaymentRequestDto paymentRequestDto, Principal principal)
            throws IamportResponseException, IOException {
        log.info("============= /payment/validate");
        log.info("paymentRequestDto = {}", paymentRequestDto);

        String impUid = paymentRequestDto.getImp_uid(); // 결재 고유번호
        int amount = Integer.parseInt(paymentRequestDto.getPaid_amount());  // 실제로 유저가 결제한 금액
        String merchant_uid = paymentRequestDto.getMerchant_uid();

        IamportResponse<Payment> irsp = paymentLookup(impUid);
        if(paymentService.validatePayment(irsp, amount, principal)) {
            return ResponseEntity.status(HttpStatus.OK).body("결재 정보 검증 완료");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결재 정보 검증 실패");
        }
    }


    //// 결재 취소
    /**
     * 결제한 금액을 취소요청이 들어오면 실행되는 메서드
     * 환불될 금액과 아임포트 서버에서 조회한 결제 금액이 다르면 환불 or 취소 안됨.
     * @param map
     * @return
     * @throws IamportResponseException
     * @throws IOException
     */
    @PostMapping("/payments/cancel")
    public IamportResponse<Payment> cancelPayments(@RequestBody Map<String,String> map) throws IamportResponseException, IOException{

        //조회
        IamportResponse<Payment> lookUp = null;
        if(map.containsKey("impUid")) lookUp = paymentLookup(map.get("impUid"));//들어온 정보에 imp_uid가 있을때
        else if(map.containsKey("paymentsNo")) lookUp = paymentLookup(map.get("paymentsNo"));//imp_uid가 없을때
        if(lookUp == null) {
            throw new IOException();
        }

        String code = paymentService.code(map.get("refund_bank"));//은행코드
        CancelData data = paymentService.cancelData(map,lookUp,code);//취소데이터 셋업
        IamportResponse<Payment> cancel = iamportClientApi.cancelPaymentByImpUid(data);//취소

        return cancel;
    }


}
