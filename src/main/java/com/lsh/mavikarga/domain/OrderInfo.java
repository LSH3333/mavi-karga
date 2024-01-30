package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * (postgresql 에서 Order 는 예약어라서 OrderInfo 로함)
 *
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
    @Column(name = "order_info_id")
    private Long id;

    // 주문한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "orderInfo", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_info_id")
    private PaymentInfo paymentInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    // 주문 일자
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime orderDate;

    // 처리 상태
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.NOT_DONE;


    public void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setOrderInfo(this); // OrderInfo와 OrderProduct 연관관계
    }

    /**
     * 생성 메서드
     * @param user: 주문한 유저
     * @param orderProductList: 주문한 물품들
     */
    public static OrderInfo createOrderInfo(User user, List<OrderProduct> orderProductList, PaymentInfo paymentInfo, Delivery delivery) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUser(user);
        for (OrderProduct orderProduct : orderProductList) {
            orderInfo.addOrderProduct(orderProduct);
        }
        orderInfo.setOrderDate(LocalDateTime.now());
        // OrderInfo - PaymentInfo 연관관계
        orderInfo.paymentInfo = paymentInfo;
        // OrderInfo - Delivery 연관관계
        orderInfo.delivery = delivery;
        // 처리 상태 초기에 NOT_DONE
        orderInfo.orderStatus = OrderStatus.NOT_DONE;
        return orderInfo;
    }

}
