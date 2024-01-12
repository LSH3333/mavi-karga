package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 한건의 주문당 하나의 OrderInfo
 * 대신 여러개의 상품을 주문했다면 OrderProduct 가 여러개가 된다
 * 그리고 어떤 하나의 상품의 수량이 여러개라면 그것또한 OrderProduct 의 count 에
 */

@Entity
@Getter
@Setter
public class OrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderInfo_id")
    private Long id;

    // 주문한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "orderInfo", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts;

    // 주문 일자
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime orderData;

    public void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setOrderInfo(this); // OrderInfo와 OrderProduct 연관관계
    }

    /**
     * 생성 메서드
     * @param user: 주문한 유저
     * @param orderProducts: 주문한 물품들
     */
    public static OrderInfo createOrderInfo(User user, OrderProduct... orderProducts) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUser(user);
        for (OrderProduct orderProduct : orderProducts) {
            orderInfo.addOrderProduct(orderProduct);
        }
        orderInfo.setOrderData(LocalDateTime.now());
        return orderInfo;
    }

}
