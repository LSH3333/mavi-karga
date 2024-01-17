package com.lsh.mavikarga.dto;

import com.lsh.mavikarga.domain.ProductSize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 사용자가 제품 페이지 들어가서 제품 장바구니 추가 시 서버로 보내올 DTO
@Data
public class OrderProductDto {

    // 사용자에게 보여줄 데이터들
    private String name;
    private String description;
    private List<ProductSize> productSizeList = new ArrayList<>();

    // 사용자에게 입력 받을 데이터들
    private Long selectedProductSizeId;
    private int count = 1;


    public OrderProductDto() {}

    public OrderProductDto(String name, String description, List<ProductSize> productSizeList) {
        this.name = name;
        this.description = description;
        // available 한 ProductSize 만 담는다
        for (ProductSize productSize : productSizeList) {
            if (productSize.isAvailable()) {
                this.productSizeList.add(productSize);
            }
        }
    }
}
