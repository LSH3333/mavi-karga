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

    @OneToOne(mappedBy = "cart", fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<ProductSize> productSizes = new ArrayList<>();





    public Cart() {}

    public void addProductSizeToCart(ProductSize productSize) {
        this.productSizes.add(productSize);
        productSize.setCart(this);
    }
}
