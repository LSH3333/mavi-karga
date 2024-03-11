package com.lsh.mavikarga.dto;

// 상품 취소/반품/교환 문의 DTO

import lombok.Data;

@Data
public class CustomerInquiryReturnDto {

    private String name;

    private String phone;

    private String orderLookUpNumber;
    // 사유
    private String reason;



    public CustomerInquiryReturnDto() {}

}
