package com.lsh.mavikarga.dto;

import lombok.Data;

@Data
public class CartProductDto {

    //
    private String name;
    private int price;

    //////// 장바구니 폼에서 사용자가 변경 가능
    private int count; // 상품 구매 갯수
    private boolean deleted; // 장바구니 폼에서 사용자가 이 제품 제거했는지 여부


    public CartProductDto(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
