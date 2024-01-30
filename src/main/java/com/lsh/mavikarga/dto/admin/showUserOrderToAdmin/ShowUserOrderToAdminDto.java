package com.lsh.mavikarga.dto.admin.showUserOrderToAdmin;

import com.lsh.mavikarga.domain.Delivery;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 사용자의 하나의 주문정보
@Data
public class ShowUserOrderToAdminDto {

    //// OrderInfo
    //  주문정보 id
    private Long orderInfoId;
    // 주문 일자
    private LocalDateTime orderDate;

    // 배송 정보
    private Delivery delivery;

    // 구매한 상품들 정보
    private List<ShowUserOrderToAdminOrderProductDto> showUserOrderToAdminOrderProductDtoList = new ArrayList<>();



}
