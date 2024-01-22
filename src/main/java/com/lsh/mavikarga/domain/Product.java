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

    // 상품 존재 여부, Product를 제거하면 연관관계 등으로 문제 생길수 있기 때문에 제거하지는 않고 removed=true 로
    // 해서 admin, 사용자에게는 보이지 않도록 함
    private boolean removed = false;

    public Product() {
    }


    public Product(AddProductDto addProductDto) {
        this.name = addProductDto.getName();
        this.price = addProductDto.getPrice();
        this.description = addProductDto.getDescription();
        initSizes();
    }

    // 존재해야 하는 모든 사이즈 초기에 만들어 놓음
    private void initSizes() {
        this.sizes.add(new ProductSize("XS", this));
        this.sizes.add(new ProductSize("S", this));
        this.sizes.add(new ProductSize("M", this));
        this.sizes.add(new ProductSize("L", this));
        this.sizes.add(new ProductSize("XL", this));
        this.sizes.add(new ProductSize("XXL", this));
    }

    // Product 가 보유하는 ProductSize 중 selectedSize available 처리
    public void setSizeAvailable(String selectedSize) {
        for (ProductSize size : sizes) {
            if (size.getSize().equals(selectedSize)) {
                size.setAvailable(true);
                return;
            }
        }
    }

    // 선택된 사이즈들은 available=true 처리, 나머지는 false 처리
    public void updateAvailableSizes(List<String> selectedSizes) {
        // 먼저 모든 productSize.available=false 처리
        for (ProductSize productSize : this.sizes) {
            productSize.setAvailable(false);
        }

        // 선택된 사이즈들 available=true 처리
        for (String selectedSize : selectedSizes) {
            setSizeAvailable(selectedSize);
        }
    }
}
