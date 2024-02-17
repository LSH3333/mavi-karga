package com.lsh.mavikarga.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CartProductDto {

    // 장바구니 ID
    private Long cartId;
    // 상품 ID
    private UUID productId;

    //////// 사용자에게 보여줄 필드
    private String name;
    private int price;
    // 썸네일 이미지
    private String thumbnail_url;

    //////// 장바구니 폼에서 사용자가 변경 가능한 필드
    private int count; // 상품 구매 갯수



    public CartProductDto() {}

    // 회원용 생성자
    public CartProductDto(Long cartId, UUID productId, String name, int price, int count, String thumbnail_url) {
        this.cartId = cartId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.count = count;
        this.thumbnail_url = thumbnail_url;
    }
    // 비회원용 생성자
    public CartProductDto(String name, UUID productId, int price, int count, String thumbnail_url) {
        this.name = name;
        this.productId = productId;
        this.price = price;
        this.count = count;
        this.thumbnail_url = thumbnail_url;
    }
}
