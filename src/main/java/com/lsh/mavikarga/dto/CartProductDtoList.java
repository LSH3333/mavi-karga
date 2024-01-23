package com.lsh.mavikarga.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class CartProductDtoList {

    // 상품 정보들 담긴 리스트
    private List<CartProductDto> cartProductDtoList = new ArrayList<>();

    // 장바구니 전체 상품 총 금액
    private int totalAmount;

    public CartProductDtoList(List<CartProductDto> cartProductDtoList) {
        this.cartProductDtoList = cartProductDtoList;
    }

    public static CartProductDtoList createForPaymentForm(List<CartProductDto> cartProductDtoList) {
        CartProductDtoList result = new CartProductDtoList(cartProductDtoList);
        for (CartProductDto cartProductDto : cartProductDtoList) {
            result.totalAmount += cartProductDto.getCount() * cartProductDto.getPrice();
            log.info("cartProductDto = {}, {}", cartProductDto.getPrice(), cartProductDto.getCount());
        }
        return result;
    }
}
