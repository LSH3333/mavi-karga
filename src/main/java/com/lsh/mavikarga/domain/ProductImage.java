package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

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

    // 0: 썸네일 아님
    // 1: 썸네일 앞면
    // 2: 썸네일 뒷면
    private int thumbnail;

    public ProductImage() {}

    public ProductImage(String url, Product product, int thumbnail) {
        this.url = url;
        this.product = product;
        this.product.getProductImages().add(this);
        this.thumbnail = thumbnail;
    }
}
