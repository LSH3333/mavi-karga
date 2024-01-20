package com.lsh.mavikarga.domain;

import jakarta.persistence.*;

@Entity
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // todo: 실제 이미지 파일 db 에 직접 저장할지, aws s3 같은 곳, 서버에 저장할지 고민 중
}
