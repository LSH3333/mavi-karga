package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
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
    private String postcode;
    // 도로명주소
    private String roadAddress;
    // 지번
    private String jibunAddress;
    // 상세주소
    private String detailAddress;
    // 참고항목
    private String extraAddress;



    public Delivery() {}

    public Delivery(String name, String email, String phone, String postcode, String roadAddress, String jibunAddress,
                       String detailAddress, String extraAddress) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.detailAddress = detailAddress;
        this.extraAddress = extraAddress;
    }
}
