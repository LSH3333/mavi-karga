package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

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

    // 결재 상품 리스트
    @OneToMany(mappedBy = "paymentInfo", fetch = FetchType.LAZY)
    @OrderBy("createdTime desc")
    private List<Product> products;

    // 결재한 User
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;


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
