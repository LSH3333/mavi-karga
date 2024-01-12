package com.lsh.mavikarga.domain;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    // 상품명
    private String name;

    // 금액
    private int price;

    // Product description
    private String description;

    // Product size (if applicable)
    private String size;

    // 재고 여부
    private boolean available;

    // todo: 제품 이미지. 이 부분은 실제 제품 보여주는 페이지가 어떨지 보고 수정해야 할듯. 그리고 관리자가 수정하는 기능 만들지 등 ..
    private String imageUrl;

    // 주문 정보
    @ManyToOne
    @JoinColumn(name="orderInfo_id")
    private OrderInfo orderInfo;


}
