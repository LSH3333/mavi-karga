package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class PaymentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="payment_info_id", nullable = false)
    private Long id;

    @OneToOne(mappedBy = "paymentInfo")
    private OrderInfo orderInfo;



    @Column(nullable = false, length = 100)
    private String payMethod;

    @Column(nullable = false, length = 100)
    private String impUid;

    @Column(nullable = false)
    private String merchantUid;

    // 결재 금액
    @Column(nullable = false)
    private int amount;

    @Column(nullable = true)
    private String buyerAddr;

    @Column(nullable = true)
    private String buyerPostcode;

    // 결제 시간
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdTime;



    public PaymentInfo() {}

    public PaymentInfo(String payMethod, String impUid, String merchantUid, int amount, String buyerAddr, String buyerPostcode, LocalDateTime createdTime) {
        this.payMethod = payMethod;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.buyerAddr = buyerAddr;
        this.buyerPostcode = buyerPostcode;
        this.createdTime = createdTime;
    }

    public void setInfos(String payMethod, String impUid, String merchantUid, int amount, String buyerAddr, String buyerPostcode, LocalDateTime createdTime) {
        this.payMethod = payMethod;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.buyerAddr = buyerAddr;
        this.buyerPostcode = buyerPostcode;
        this.createdTime = createdTime;
    }
}
