package com.lsh.mavikarga.dto.admin.showUserOrderToAdmin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 사용자의 주문 목록 뷰를 위해 사용, thymeleaf 랜더링 위한 리스트 dto
// AdminController.viewUserOrder()
// 리스트 구조: ShowUserOrderToAdminDtoList -> ShowUserOrderToAdminDto -> ShowUserOrderToAdminOrderProductDto
@Data
public class ShowUserOrderToAdminDtoList {


    // 제품 하나 당 하나
    private List<ShowUserOrderToAdminDto> showUserOrderToAdminDtoList = new ArrayList<>();
}
