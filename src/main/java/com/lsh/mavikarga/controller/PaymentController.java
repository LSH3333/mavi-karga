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
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
@Slf4j
public class PaymentController {

    @Value("${PORT_ONE_API_KEY}")
    private String portOneApiKey;

    @Value("${PORT_ONE_SECRET_KEY}")
    private String portOneSecretKey;

    // https://github.com/iamport/iamport-rest-client-java
    private IamportClient iamportClientApi;
    private final PaymentService paymentService;

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public PaymentController(PaymentService paymentService, UserService userService, OrderService orderService) {
        this.paymentService = paymentService;

        this.userService = userService;
        this.orderService = orderService;
    }

    @PostConstruct
    public void injectIamportClientApi() {
        this.iamportClientApi = new IamportClient(portOneApiKey,
                portOneSecretKey);
    }

    // 결재 성공 폼
    @GetMapping("/payments/paymentSuccess")
    public String paymentSuccessForm(Principal principal, Model model, @RequestParam String orderLookUpNumber) {

        log.info("orderLookUpNumber = {}", orderLookUpNumber);
        model.addAttribute("orderLookUpNumber", orderLookUpNumber);
        return "payments/paymentSuccess";

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

    // 회원 결제 페이지
    @GetMapping("/payments/payment")
    public String paymentForm(Principal principal, Model model, HttpSession session) {

        if(principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);

            // 사용자 장바구니 담긴 상품들 보여주기
            CartProductDtoList cartProductDtoList = CartProductDtoList.createForPaymentForm(orderService.createCartProductDtoList(user.getId()));
            model.addAttribute("cartProductDtoList", cartProductDtoList);
        } else {
            // 비회원 장바구니 담긴 상품들 보여주기
            CartProductDtoList cartProductDtoList = CartProductDtoList.createForPaymentForm(orderService.createCartProductDtoListForNonUser(session));
            model.addAttribute("cartProductDtoList", cartProductDtoList);
        }


        return "payments/payment";
    }

    // 비회원 결제 페이지
//    @GetMapping("/payments/payment/nonuser")
//    public String paymentFormNonUser(Model model, HttpSession session) {
//

//
//        return "payments/paymentNonUser";
//    }

    // 클라이언트에서 회원 결재요청 성공 후 받는 end point
//    @PostMapping("/payments/validate")
//    private ResponseEntity<String> validatePayment(@ModelAttribute @Valid PaymentRequestDto paymentRequestDto, Principal principal, BindingResult bindingResult)
//            throws IamportResponseException, IOException {
//
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.badRequest().body("Validation errors: " + bindingResult.getAllErrors());
//        }
//
//        log.info("============= /payment/validate");
//        log.info("paymentRequestDto = {}", paymentRequestDto);
//
//        String impUid = paymentRequestDto.getImp_uid(); // 결재 고유번호
//        int amount = Integer.parseInt(paymentRequestDto.getPaid_amount());  // 실제로 유저가 결제한 금액
//        String merchant_uid = paymentRequestDto.getMerchant_uid();
//
//        IamportResponse<Payment> irsp = paymentLookup(impUid);
//        // 결제 성공 시 주문 조회 번호 클라이언트로 보냄
//        String orderLookUpNumber = paymentService.validatePayment(irsp, paymentRequestDto, principal);
//        if(orderLookUpNumber != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(orderLookUpNumber);
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결재 정보 검증 실패");
//        }
//    }

    // 클라이언트에서 비회원 결재요청 성공 후 받는 end point
    @PostMapping("/payments/validate")
    private ResponseEntity<String> validatePayment(@ModelAttribute @Valid PaymentRequestDto paymentRequestDto,
                                                          HttpSession session, BindingResult bindingResult, Principal principal)
            {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation errors: " + bindingResult.getAllErrors());
        }

        // 배송 정보 미리 저장
        String orderLookUpNumber = paymentService.storeOrder(paymentRequestDto, session, principal);
        if(orderLookUpNumber != null) {
            return ResponseEntity.status(HttpStatus.OK).body(orderLookUpNumber);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 정보 저장 실패");
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


    // 포트원 웹훅 엔드포인트
    @PostMapping("/portone-webhook")
    public ResponseEntity<String> portOneWebhook(@RequestParam String status, @RequestParam String imp_uid, @RequestParam String merchant_uid,
                                                  HttpSession session) throws IamportResponseException, IOException {
        log.info("portOneWebhook = {}, {}, {}", status, merchant_uid, imp_uid);

        if (status.equals("paid")) {

            log.info("============= /payment/validate/nonuser");

            IamportResponse<Payment> irsp = paymentLookup(imp_uid);
            String orderLookUpNumber = paymentService.validateWebHook(irsp);

            if(orderLookUpNumber != null) {
                return ResponseEntity.status(HttpStatus.OK).body(orderLookUpNumber);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결재 정보 검증 실패");
            }

        }

        return null;
    }
}
