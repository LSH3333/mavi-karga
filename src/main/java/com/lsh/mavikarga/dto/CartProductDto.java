package com.lsh.mavikarga.dto;

import lombok.Data;

@Data
public class CartProductDto {

    //
    private String name;
    private int price;

    private int count;


    public CartProductDto(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
