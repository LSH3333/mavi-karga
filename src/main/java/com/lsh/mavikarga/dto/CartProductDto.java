package com.lsh.mavikarga.dto;

import lombok.Data;

@Data
public class CartProductDto {

    private Long cartId;
    //////// 사용자에게 보여줄 필드
    private String name;
    private int price;

    //////// 장바구니 폼에서 사용자가 변경 가능한 필드
    private int count; // 상품 구매 갯수
    private boolean deleted = false; // 장바구니 폼에서 사용자가 이 제품 제거했는지 여부

    public CartProductDto() {}

    // 회원용 생성자
    public CartProductDto(Long cartId, String name, int price, int count) {
        this.cartId = cartId;
        this.name = name;
        this.price = price;
        this.count = count;
    }
    // 비회원용 생성자
    public CartProductDto(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
