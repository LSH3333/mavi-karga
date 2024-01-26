package com.lsh.mavikarga.dto;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.ProductSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private boolean available;
    // 상품 관리 방법 (세탁 방법 등)
    private String detailsAndCare;

    private List<String> sizes = new ArrayList<>();


}
