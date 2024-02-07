package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 이미지 저장 경로 (AWS S3)
    // https://mavikarga-bucket.s3.ap-northeast-2.amazonaws.com/images/baekjoon.png
    private String url;


    public ProductImage() {}

    public ProductImage(String url, Product product) {
        this.url = url;
        this.product = product;
        this.product.getProductImages().add(this);
    }
}
