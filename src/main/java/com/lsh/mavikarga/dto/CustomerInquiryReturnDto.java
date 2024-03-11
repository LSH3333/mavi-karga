package com.lsh.mavikarga.dto;

// 상품 취소/반품/교환 문의 DTO

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerInquiryReturnDto {

    private String name;

    private String phone;

    private String orderLookUpNumber;
    // 사유
    private String reason;

    private LocalDateTime createdTime;


    public CustomerInquiryReturnDto() {}

    public CustomerInquiryReturnDto(String name, String phone, String orderLookUpNumber, String reason, LocalDateTime createdTime) {
        this.name = name;
        this.phone = phone;
        this.orderLookUpNumber = orderLookUpNumber;
        this.reason = reason;
        this.createdTime = createdTime;
    }
}
