package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.dto.CustomerInquiryReturnDto;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

// 고객 문의

@Entity
@Data
public class CustomerInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_inquiry_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(length = 100, nullable = false)
    private String orderLookUpNumber;

    // 사유
    @Lob
    private String reason;

    // 문의 시간
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdTime;




    public CustomerInquiry() {}

    public CustomerInquiry(CustomerInquiryReturnDto customerInquiryReturnDto) {
        this.name = customerInquiryReturnDto.getName();
        this.phone = customerInquiryReturnDto.getPhone();
        this.orderLookUpNumber = customerInquiryReturnDto.getOrderLookUpNumber();
        this.reason = customerInquiryReturnDto.getReason();
        this.createdTime = LocalDateTime.now();
    }
}
