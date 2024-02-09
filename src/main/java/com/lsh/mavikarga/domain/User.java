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
    @Column(name = "user_id")
    private Long id;

    // 주문 리스트
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<OrderInfo> orderInfos = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();




    @NotEmpty(message = "비어있을수 없습니다")
    private String username;
    @NotEmpty(message = "비어있을수 없습니다")
    private String password;

    private String role; // ROLE_USER, ROLE_ADMIN

    @NotEmpty(message = "비어있을수 없습니다")
    private String email;
    private String provider; // google, kakao ..
    private String provider_id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdTime;





    public User() {}

    // oauth2 register
    public User(String username, String password, String email, String role, String provider, String providerId, LocalDateTime createdTime) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.provider_id = providerId;
        this.createdTime = createdTime;
    }


}
