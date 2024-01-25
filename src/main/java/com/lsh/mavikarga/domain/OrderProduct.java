package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * OrderInfo 와 ProductSize 사이 다대다 관계 해소 위한 중간 클래스
 */

@Entity
@Getter
@Setter
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_info_id")
    private OrderInfo orderInfo;

    // 구매한 상품 개당 가격
    private int orderPrice;
    // 구매한 갯수
    private int count;

    public static OrderProduct createOrderProduct(ProductSize productSize, int orderPrice, int count) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProductSize(productSize);
        orderProduct.setOrderPrice(orderPrice);
        orderProduct.setCount(count);
        return orderProduct;
    }

    // 주문 가격 전체 조회
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
