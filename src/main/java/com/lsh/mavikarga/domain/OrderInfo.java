package com.lsh.mavikarga.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class OrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderInfo_id")
    private Long id;

    // 주문한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 주문한 상품 리스트
    @OneToMany(mappedBy = "orderInfo")
    private List<Product> products;

    // 결재 정보
    @OneToOne
    @JoinColumn(name="paymentInfo_id")
    private PaymentInfo paymentInfo;


}
