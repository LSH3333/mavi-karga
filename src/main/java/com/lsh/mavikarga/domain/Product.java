package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.dto.AddProductDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Product {

    @Id
    @UuidGenerator
    @Column(name = "product_id")
    private UUID id;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductSize> sizes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    // 상품명
    private String name;

    // 금액
    private int price;

    // Product description
    private String description;

    // 상품 관리 방법 (세탁 방법 등)
    private String detailsAndCare;

    // 상품 존재 여부, Product를 제거하면 연관관계 등으로 문제 생길수 있기 때문에 제거하지는 않고 removed=true 로
    // 해서 admin, 사용자에게는 보이지 않도록 함
    private boolean removed = false;

    public Product() {
    }


    public Product(AddProductDto addProductDto) {
        this.name = addProductDto.getName();
        this.price = addProductDto.getPrice();
        this.description = addProductDto.getDescription();
        this.detailsAndCare = addProductDto.getDetailsAndCare();
        // ProductSize 생성, 재고 있음 처리
        for (String selectedSize : addProductDto.getSizes()) {
            ProductSize productSize = new ProductSize(selectedSize, this);
            this.sizes.add(productSize);
            productSize.setAvailable(true);
        }

    }


    // Product 가 보유하는 ProductSize 중 selectedSize available 처리
    // Product 가 selectedSize 의 ProductSize 보유중이면 return true, else false
    public boolean setSizeAvailable(String selectedSize) {
        for (ProductSize size : sizes) {
            if (size.getSize().equals(selectedSize)) {
                size.setAvailable(true);
                return true;
            }
        }
        return false;
    }

    // 선택된 사이즈들은 available=true 처리, 나머지는 false 처리
    public void updateAvailableSizes(List<String> selectedSizes) {
        // 먼저 모든 productSize.available=false 처리
        for (ProductSize productSize : this.sizes) {
            productSize.setAvailable(false);
        }

        // productSize 존재하는데 재고없음 처리 된 것 : 재고없음 처리
        // productSize 없는데 재고있음 처리 된 것 : 새로운 ProductSize 만듦
        for (String selectedSize : selectedSizes) {
            // selectedSize 인 productSize 재고 있음 처리 그런데 productSize 가 없다면 새롭게 만듦
            if (!setSizeAvailable(selectedSize)) {
                ProductSize productSize = new ProductSize(selectedSize, this);
                this.sizes.add(productSize);
                productSize.setAvailable(true);
            }
        }
    }
}
