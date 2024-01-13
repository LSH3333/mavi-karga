package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.dto.AddProductDto;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;

    public Product() {}

    // 관리자 상품 추가용
    public Product(AddProductDto addProductDto) {
        this.name = addProductDto.getName();
        this.price = addProductDto.getPrice();
        this.description = addProductDto.getDescription();
        this.size = addProductDto.getSize();
        this.available = addProductDto.isAvailable();
    }

    public void updateWithAddProductDto(AddProductDto addProductDto) {
        this.name = addProductDto.getName();
        this.price = addProductDto.getPrice();
        this.description = addProductDto.getDescription();
        this.size = addProductDto.getSize();
        this.available = addProductDto.isAvailable();
    }
}
