package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="app_user") // postgresql 에서 "user" 는 예약어
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    // 주문 리스트
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<OrderInfo> orderInfos = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();




    @NotEmpty(message = "비어있을수 없습니다")
    @Column(length = 100, nullable = false)
    private String username;

    @NotEmpty(message = "비어있을수 없습니다")
    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String role; // ROLE_USER, ROLE_ADMIN

//    @NotEmpty(message = "비어있을수 없습니다")
//    private String email;
    @Column(length = 100, nullable = false)
    private String provider; // google, kakao ..
    @Column(length = 100, nullable = false)
    private String provider_id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdTime;

    // 회원 삭제 (탈퇴) 여부
    @Column(nullable = false)
    private boolean deleted = false;

    // 마지막 로그인 시간
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastLoggedIn;



    public User() {}

    // oauth2 register
    public User(String username, String password, String role, String provider, String providerId, LocalDateTime createdTime) {
        this.username = username;
        this.password = password;
//        this.email = email;
        this.role = role;
        this.provider = provider;
        this.provider_id = providerId;
        this.createdTime = createdTime;
        this.deleted = false;
        this.lastLoggedIn = LocalDateTime.now();
    }

    // 회원 삭제 (탈퇴)
    // 실제로 테이블 삭제하지는 않고 정보들 제거처리
    public void delete() {
        this.username = "DELETED";
        this.password = "DELETED";
//        this.email = "DELETED";
        this.provider = "DELETED";
        this.provider_id = "DELETED";
        this.deleted = true;
    }


}
