package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize;


    // 제품 갯수
    private int count;


    public Cart() {}

    public Cart(ProductSize productSize, int count, User user) {
        this.productSize = productSize;
        this.productSize.getCarts().add(this);

        this.user = user;
        this.user.getCarts().add(this);

        this.count = count;
    }

}
