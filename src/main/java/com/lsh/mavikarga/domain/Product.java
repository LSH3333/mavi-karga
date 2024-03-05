package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.enums.ClothingCategory;
import com.lsh.mavikarga.enums.ProductColor;
import com.lsh.mavikarga.enums.Sizes;
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
    @Column(name = "product_id", nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductOption> productOptions = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    // 썸네일
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "thumbnail_front_id")
    private ProductImage thumbnail_front;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "thumbnail_back_id")
    private ProductImage thumbnail_back;

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
    @Lob
    private String detailsAndCare;

    // 상품 존재 여부, Product를 제거하면 연관관계 등으로 문제 생길수 있기 때문에 제거하지는 않고 removed=true 로
    // 해서 admin, 사용자에게는 보이지 않도록 함
    private boolean removed = false;

    // 카테고리
    @Enumerated(EnumType.STRING)
    private ClothingCategory clothingCategory;
    // 메인에 디스플레이할 상품인지 여부
    private boolean mainProduct;

    // 이 값이 작을수록 상품 화면 상단에 디스플레이됨
    private int displayOrder;



    public Product() {
    }


    public Product(AddProductDto addProductDto) {
        this.name = addProductDto.getName();
        this.price = addProductDto.getPrice();
        this.price_USD = addProductDto.getPrice_USD();
        this.description = addProductDto.getDescription();
        this.detailsAndCare = addProductDto.getDetailsAndCare();
        this.clothingCategory = addProductDto.getClothingCategory();
        this.mainProduct = addProductDto.isMainProduct();
        this.displayOrder = addProductDto.getDisplayOrder();

        // ProductSize 생성, 재고 있음 처리
        // 크기별 색상별로 제품크기 생성.
        // ex) 사이즈 M & 색상 red, 사이즈 M & 색상 blue ...
        // 크기별
        for (Sizes selectedSize : addProductDto.getSize()) {
            // 색상별
            for (ProductColor productColor : addProductDto.getProductColor()) {
                ProductOption productSize = new ProductOption(selectedSize, productColor,this);
                this.productOptions.add(productSize);
                productSize.setAvailable(true);
            }
        }
    }

    // 현재 DB에 존재하는 ProductSize 순회하면서 선택된 크기, 색상에 맞는 ProductSize 재고 있음 처리
    private boolean setAvailableSelectedProductSize(Sizes selectedSize, ProductColor selectedColor) {
        for (ProductOption size : this.productOptions) {
            if (size.getSize().equals(selectedSize) && size.getProductColor() == selectedColor) {
                size.setAvailable(true);
                return true;
            }
        }
        return false;
    }

    // 선택된 사이즈들은 available=true 처리, 나머지는 false 처리
    public void updateAvailableSizes(List<Sizes> selectedSizes, List<ProductColor> selectedProductColor) {
        // 먼저 모든 productSize.available=false 처리
        for (ProductOption productSize : this.productOptions) {
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
        for (Sizes selectedSize : selectedSizes) {
            for (ProductColor selectedColor : selectedProductColor) {
                // 현재 DB에 존재하는 ProductSize 순회하면서 선택된 크기, 색상에 맞는 ProductSize 재고 있음 처리
                if(!setAvailableSelectedProductSize(selectedSize, selectedColor)) {
                    // 만약 기존 DB에 없던 새로운 크기,색상 조합이라면 새롭게 만듦
                    ProductOption productSize = new ProductOption(selectedSize, selectedColor, this);
                    this.productOptions.add(productSize);
                    productSize.setAvailable(true);
                }
            }
        }

    }
}
