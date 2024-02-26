package com.lsh.mavikarga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 포트원 결재
 * 클라이언트에서 보내오는 실제 클라이언트에서 결재한 정보 Dto
 * 결재 금액 맞는 지 등 검증 위함
 */
@Data
public class PaymentRequestDto {
//    private String imp_uid;
//    private String paid_amount;
//    private String merchant_uid;

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
    @NotBlank(message = "Name is required")
    private String postcode;
    // 도로명주소
    @NotBlank(message = "roadAddress is required")
    private String roadAddress;
    // 지번
    @NotBlank(message = "jibunAddress is required")
    private String jibunAddress;
    // 상세주소
    @NotBlank(message = "detailAddress is required")
    private String detailAddress;
    // 참고항목
    @NotBlank(message = "extraAddress is required")
    private String extraAddress;
}
