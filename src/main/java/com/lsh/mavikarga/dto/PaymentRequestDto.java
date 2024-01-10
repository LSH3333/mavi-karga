package com.lsh.mavikarga.dto;

import lombok.Data;

/**
 * 포트원 결재
 * 클라이언트에서 보내오는 실제 클라이언트에서 결재한 정보
 * 결재 금액 맞는 지 등 검증 위함
 */
@Data
public class PaymentRequestDto {
    private String imp_uid;
    private String pay_method;
    private String merchant_uid;

//    private String paid_amount;
//    private String name;
//    private String pg_provider;
//    private String buyer_email;
//    private String buyer_tel;
//    private String paid_at;
}
