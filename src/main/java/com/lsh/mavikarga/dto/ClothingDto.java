package com.lsh.mavikarga.dto;

import com.lsh.mavikarga.domain.ProductImage;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.UUID;

// 전체 상품 페이지 DTO
@Data
public class ClothingDto {
    // 상품 ID
    private UUID productId;
    // 상품명
    private String name;
    // 금액
    private int price;
    // 썸네일 앞면
    private String  thumbnail_front_url;
    // 썸네일 뒷면
    private String thumbnail_back_url;

    public ClothingDto() {}

    public ClothingDto(UUID productId, String name, int price, String thumbnail_front_url, String thumbnail_back_url) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.thumbnail_front_url = thumbnail_front_url;
        this.thumbnail_back_url = thumbnail_back_url;
    }
}
