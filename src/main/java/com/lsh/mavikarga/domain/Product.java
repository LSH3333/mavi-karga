package com.lsh.mavikarga.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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

    // Product category (e.g., shirts, pants, shoes)
    private String category;

    // Product size (if applicable)
    private String size;

    // Product availability (e.g., in stock, out of stock)
    private boolean available;

    // todo: 제품 이미지. 이 부분은 실제 제품 보여주는 페이지가 어떨지 보고 수정해야 할듯. 그리고 관리자가 수정하는 기능 만들지 등 ..
    private String imageUrl;
}
