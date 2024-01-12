package com.lsh.mavikarga.dto;

import lombok.Data;

/**
 * 관리자 페이지에서 상품추가 폼 보내는 DTO
 */
@Data
public class AddProductDto {
    private String name;
    private int price;
    private String description;
    private String size;
    private boolean available;
}
