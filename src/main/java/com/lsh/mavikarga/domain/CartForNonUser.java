package com.lsh.mavikarga.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 비회원 장바구니 객체
@Data
public class CartForNonUser {

    List<ProductSize> productSizeList = new ArrayList<>();
    List<Integer> countList = new ArrayList<>();

//    private ProductSize productSize;
    // 제품 갯수
//    private int count;
}
