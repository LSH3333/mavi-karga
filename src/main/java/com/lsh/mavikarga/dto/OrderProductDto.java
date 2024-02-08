package com.lsh.mavikarga.dto;

import com.lsh.mavikarga.domain.ProductSize;
import com.lsh.mavikarga.enums.ProductColor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// 사용자가 제품 페이지 들어가서 제품 장바구니 추가 시 서버로 보내올 DTO
@Data
public class OrderProductDto  {

    // 사용자에게 보여줄 데이터들
    private String name;
    private String description;
    private List<ProductSize> productSizeList = new ArrayList<>();
    private int price;
//    private int price_USD;

    // 상품 이미지 url (aws s3 버킷)
    private List<String> productImgUrlList = new ArrayList<>();

    // 사용자에게 입력 받을 데이터들
    private Long selectedProductSizeId;
    private int count = 1;



    public OrderProductDto() {}

    public OrderProductDto(String name, String description, List<ProductSize> productSizeList, List<String> productImgUrlList,
                           int price) {
        // 상품명
        this.name = name;
        // 상품 상세
        this.description = description;
        // 가격
        this.price = price;
        // available 한 ProductSize 만 담는다
        for (ProductSize productSize : productSizeList) {
            if (productSize.isAvailable()) {
                this.productSizeList.add(productSize);
            }
        }
        // 상품 이미지
        this.productImgUrlList = productImgUrlList;
    }

}
