package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class PaymentInfo {
    @Id
    @GeneratedValue
    @Column(name="paymentInfo_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String payMethod;

    @Column(nullable = false, length = 100)
    private String impUid;

    @Column(nullable = false, length = 100)
    private String merchantUid;

    // 결재 금액
    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, length = 100)
    private String buyerAddr;

    @Column(nullable = false, length = 100)
    private String buyerPostcode;

    // 결재 시간
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdTime;

    // 주문 정보
    @OneToOne(mappedBy = "paymentInfo")
    private OrderInfo orderInfo;

    public PaymentInfo() {}

    public PaymentInfo(String payMethod, String impUid, String merchantUid, int amount, String buyerAddr, String buyerPostcode) {
        this.payMethod = payMethod;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.buyerAddr = buyerAddr;
        this.buyerPostcode = buyerPostcode;
    }

}