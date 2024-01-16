package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_size_id")
    private Long id;

    private String size;

    private boolean available = false;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductSize() {}

    public ProductSize(String size, Product product) {
        this.size = size;
        this.product = product;
        this.available = false; // 최초에는 재고 없음 처리
    }
}
