package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, length = 100)
    private String buyerAddr;

    @Column(nullable = false, length = 100)
    private String buyerPostcode;


    // todo: 결재 User


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
