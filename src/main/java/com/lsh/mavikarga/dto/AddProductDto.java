package com.lsh.mavikarga.dto;

import com.lsh.mavikarga.enums.ClothingCategory;
import com.lsh.mavikarga.enums.ProductColor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 관리자 페이지에서 상품추가 폼 보내는 DTO
 */
@Data
public class AddProductDto {

    @NotEmpty(message = "비어있을수 없습니다")
    private String name;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private int price;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private int price_USD;

    @NotEmpty(message = "비어있을수 없습니다")
    private String description;

    private boolean available;

    // 상품 관리 방법 (세탁 방법 등)
    private String detailsAndCare;

    // 상품 사이즈 "S", "M", "L" ...
    private List<String> sizes = new ArrayList<>();

    // 선택한 상품 색상 목록
    private List<ProductColor> productColor = new ArrayList<>();
    // 상품 색상 enum 목록
    private ProductColor[] productColors = ProductColor.values();

    // 선택한 상품 카테고리
    private ClothingCategory clothingCategory;
    // 상품 카테고리 enum 목록
    private ClothingCategory[] clothingCategories = ClothingCategory.values();



}
