package com.lsh.mavikarga.dto;

import lombok.Data;

// 사용자가 제품 페이지 들어가서 제품 장바구니 추가 시 서버로 보내올 DTO
@Data
public class OrderProductDto {
    private Long productId;
    private int count;
}
