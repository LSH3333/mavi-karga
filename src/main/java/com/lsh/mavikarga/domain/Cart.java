package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;

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
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;


    // 제품 갯수
    private int count;


    public Cart() {}

    public Cart(ProductOption productOption, int count, User user) {
        this.productOption = productOption;
        this.productOption.getCarts().add(this);

        this.user = user;
        this.user.getCarts().add(this);

        this.count = count;
    }

}
