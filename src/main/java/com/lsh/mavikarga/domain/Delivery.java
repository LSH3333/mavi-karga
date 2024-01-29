package com.lsh.mavikarga.domain;

import jakarta.persistence.*;


@Entity
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    // 이름
    private String name;
    // 이메일
    private String email;
    // 휴대전화
    private String phone;

    // 우편번호
    private String address1;
    // 주소
    private String address2;
    // 상세주소
    private String address3;

}
