package com.lsh.mavikarga.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartProductDtoList {

    private List<CartProductDto> cartProductDtoList;

    public CartProductDtoList(List<CartProductDto> cartProductDtoList) {
        this.cartProductDtoList = cartProductDtoList;
    }
}
