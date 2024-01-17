package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.dto.AddProductDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    // 상품명
    private String name;

    // 금액
    private int price;

    // Product description
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductSize> sizes = new ArrayList<>();

    // todo: 제품 이미지. 이 부분은 실제 제품 보여주는 페이지가 어떨지 보고 수정해야 할듯. 그리고 관리자가 수정하는 기능 만들지 등 ..
    private String imageUrl;

    // 상품 존재 여부, Product를 제거하면 연관관계 등으로 문제 생길수 있기 때문에 제거하지는 않고 removed=true 로
    // 해서 admin, 사용자에게는 보이지 않도록 함
    private boolean removed=false;

    public Product() {}


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
