package com.lsh.mavikarga.dto;

import com.lsh.mavikarga.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MyPageDto {
    // 상품 ID
    private UUID productId;
    // 상품명
    private String name;
    // 주문 일자
    private LocalDateTime orderDate;
    // 구매한 상품 개당 가격
    private int orderPrice;
    // 구매한 갯수
    private int count;
    // 처리 상태
    private OrderStatus orderStatus;

    // 썸네일 이미지
    private String thumbnail_url;

}
