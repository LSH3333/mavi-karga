package com.lsh.mavikarga.dto;

import lombok.Data;

// 결재 시 유저 정보 DTO
@Data
public class PaymentUserInfoDto {
    // 이름
    private String name;
    // 이메일
    private String email;
    // 휴대전화
    private String phone;

}
