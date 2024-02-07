package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.enums.ClothingCategory;
import com.lsh.mavikarga.enums.ProductColor;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Slf4j
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
    // USD 금액
    private int price_USD;

    // Product description
    @Lob
    private String description;

    // 상품 관리 방법 (세탁 방법 등)
    private String detailsAndCare;

    // 상품 존재 여부, Product를 제거하면 연관관계 등으로 문제 생길수 있기 때문에 제거하지는 않고 removed=true 로
    // 해서 admin, 사용자에게는 보이지 않도록 함
    private boolean removed = false;

    // 카테고리
    @Enumerated(EnumType.STRING)
    private ClothingCategory clothingCategory;



    public Product() {
    }


    public Product(AddProductDto addProductDto) {
        this.name = addProductDto.getName();
        this.price = addProductDto.getPrice();
        this.price_USD = addProductDto.getPrice_USD();
        this.description = addProductDto.getDescription();
        this.detailsAndCare = addProductDto.getDetailsAndCare();
        this.clothingCategory = addProductDto.getClothingCategory();

        // ProductSize 생성, 재고 있음 처리
        // 크기별 색상별로 제품크기 생성.
        // ex) 사이즈 M & 색상 red, 사이즈 M & 색상 blue ...
        // 크기별
        for (String selectedSize : addProductDto.getSizes()) {
            // 색상별
            for (ProductColor productColor : addProductDto.getProductColor()) {
                ProductSize productSize = new ProductSize(selectedSize, productColor,this);
                this.sizes.add(productSize);
                productSize.setAvailable(true);
            }
        }
    }

    // 현재 DB에 존재하는 ProductSize 순회하면서 선택된 크기, 색상에 맞는 ProductSize 재고 있음 처리
    private boolean setAvailableSelectedProductSize(String selectedSize, ProductColor selectedColor) {
        for (ProductSize size : this.sizes) {
            if (size.getSize().equals(selectedSize) && size.getProductColor() == selectedColor) {
                size.setAvailable(true);
                return true;
            }
        }
        return false;
    }

    // 선택된 사이즈들은 available=true 처리, 나머지는 false 처리
    public void updateAvailableSizes(List<String> selectedSizes, List<ProductColor> selectedProductColor) {
        // 먼저 모든 productSize.available=false 처리
        for (ProductSize productSize : this.sizes) {
            productSize.setAvailable(false);
        }


//        log.info("UPDATE AVAILABLE");
//        for (String selectedSize : selectedSizes) {
//            log.info("selectedSize = {}", selectedSize);
//        }
//        for (ProductColor productColor : selectedProductColor) {
//            log.info("color = {}", productColor);
//        }

        // M,BLACK M,BLUE L,BLACK, L,BLUE
        // 선택된 사이즈, 색상 조합 순회하면서 DB 에서 활성화,비활성화 처리
        for (String selectedSize : selectedSizes) {
            for (ProductColor selectedColor : selectedProductColor) {
                // 현재 DB에 존재하는 ProductSize 순회하면서 선택된 크기, 색상에 맞는 ProductSize 재고 있음 처리
                if(!setAvailableSelectedProductSize(selectedSize, selectedColor)) {
                    // 만약 기존 DB에 없던 새로운 크기,색상 조합이라면 새롭게 만듦
                    ProductSize productSize = new ProductSize(selectedSize, selectedColor, this);
                    this.sizes.add(productSize);
                    productSize.setAvailable(true);
                }
            }
        }

    }
}
