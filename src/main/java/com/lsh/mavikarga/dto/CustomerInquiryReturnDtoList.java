package com.lsh.mavikarga.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerInquiryReturnDtoList {
    private List<CustomerInquiryReturnDto> customerInquiryReturnDtoList = new ArrayList<>();
    private int totalPages;
}
