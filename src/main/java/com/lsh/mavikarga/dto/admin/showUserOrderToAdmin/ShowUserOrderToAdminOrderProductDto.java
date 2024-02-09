package com.lsh.mavikarga.dto.admin.showUserOrderToAdmin;

import com.lsh.mavikarga.enums.Sizes;
import lombok.Data;

import java.util.UUID;

// 사용자의 하나의 주문정보 내부의 단일 제품
@Data
public class ShowUserOrderToAdminOrderProductDto {

    // 구매한 상품 개당 가격
    private int orderPrice;
    // 구매한 갯수
    private int count;

    //// ProductSize
    private Sizes size;

    //// Product
    private UUID productId;
    // 상품명
    private String name;

}
