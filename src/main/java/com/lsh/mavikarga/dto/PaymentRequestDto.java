package com.lsh.mavikarga.dto;

import lombok.Data;

/**
 * 포트원 결재
 * 클라이언트에서 보내오는 실제 클라이언트에서 결재한 정보 Dto
 * 결재 금액 맞는 지 등 검증 위함
 */
@Data
public class PaymentRequestDto {
    private String imp_uid;
    private String paid_amount;
    private String merchant_uid;

//    private String paid_amount;
//    private String name;
//    private String pg_provider;
//    private String buyer_email;
//    private String buyer_tel;
//    private String paid_at;

    // 이름
    private String name;
    // 이메일
    private String email;
    // 휴대전화
    private String phone;

    // 우편번호
    private String postcode;
    // 도로명주소
    private String roadAddress;
    // 지번
    private String jibunAddress;
    // 상세주소
    private String detailAddress;
    // 참고항목
    private String extraAddress;
}
