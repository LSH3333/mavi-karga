package com.lsh.mavikarga.dto;

import lombok.Data;

/**
 * 포트원 결재에서 결재 정보 담을 dto
 * 결재 금액 맞는 지 등 검증 위함
 */
@Data
public class PaymentRequestDto {
    private String imp_uid;
    private String merchant_uid;
    private String amount;
}
