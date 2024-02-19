package com.lsh.mavikarga.dto;

import lombok.Data;

import java.time.LocalDateTime;

// 관리자 콘솔, 사용자 목록 뷰를 위한 Dto
@Data
public class ShowUserToAdminDto {
    // user uuid
    private Long userId;
    // 유저명
    private String username;
    // 이메일
//    private String email;
    // 생성일
    private LocalDateTime createdTime;

    protected ShowUserToAdminDto() {}

    public ShowUserToAdminDto(Long userId, String username,  LocalDateTime createdTime) {
        this.userId = userId;
        this.username = username;
//        this.email = email;
        this.createdTime = createdTime;
    }

}
