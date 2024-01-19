package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_size_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productSize", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @OneToMany(mappedBy = "productSize")
    private List<Cart> carts = new ArrayList<>();

//    @ManyToOne
//    @JoinColumn(name = "cart_id")
//    private Cart cart;


    private String size;

    private boolean available = false;


    public ProductSize() {}

    public ProductSize(String size, Product product) {
        this.size = size;
        this.product = product;
        this.available = false; // 최초에는 재고 없음 처리
    }
}
