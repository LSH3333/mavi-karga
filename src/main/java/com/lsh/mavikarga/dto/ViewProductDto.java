package com.lsh.mavikarga.dto;

import com.lsh.mavikarga.domain.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 관리자 페이지 상품 리스트 DTO
 */
@Data
public class ViewProductDto {
    // 상품명
    private String name;

    // 금액
    private int price;

    // Product size (if applicable)
    private String size;

    // 재고 여부
    private boolean available;

    // 관리자 페이지에 보낼 상품 DTO 생성
    public static List<ViewProductDto> createViewProductDtoList(List<Product> allProducts) {
        List<ViewProductDto> viewProductDtoList = new ArrayList<>();
        for (Product product : allProducts) {
            ViewProductDto viewProductDto = new ViewProductDto();
            viewProductDto.setName(product.getName());
            viewProductDto.setPrice(product.getPrice());
            viewProductDto.setSize(product.getSize());
            viewProductDto.setAvailable(product.isAvailable());

            viewProductDtoList.add(viewProductDto);
        }
        return viewProductDtoList;
    }

}
