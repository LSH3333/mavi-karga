package com.lsh.mavikarga.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 관리자 페이지에서 상품추가 폼 보내는 DTO
 */
@Data
public class AddProductDto {
    @NotEmpty(message = "비어있을수 없습니다")
    private String name;
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private int price;
    @NotEmpty(message = "비어있을수 없습니다")
    private String description;
    private String size;
    private boolean available;
}
