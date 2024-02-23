package com.lsh.mavikarga.domain;

import lombok.Data;


// 비회원 장바구니 객체
@Data
public class CartForNonUser {

    // id
    // 비회원 장바구니 객체는 세션에의 "cartList" 에 저장됨. id는 첫상품 부터 0,1,2 ...
    private int id;
    // 제품
    private ProductSize productSize;
    // 제품 갯수
    private int count;
    // 비회원 장바구니에서 제거 여부
    private boolean removed = false;

    public CartForNonUser(int id, ProductSize productSize, int count) {
        this.id = id;
        this.productSize = productSize;
        this.count = count;
    }
}
