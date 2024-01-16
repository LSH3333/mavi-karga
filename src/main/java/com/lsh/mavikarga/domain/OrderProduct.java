package com.lsh.mavikarga.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * OrderInfo 와 Product 사이 다대다 관계 해소 위한 중간 클래스
 */

@Entity
@Getter
@Setter
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_items_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_info_id")
    private OrderInfo orderInfo;

    private int orderPrice;
    private int count;

    public static OrderProduct createOrderProduct(Product product, int orderPrice, int count) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setOrderPrice(orderPrice);
        orderProduct.setCount(count);
        return orderProduct;
    }



    // 주문 가격 전체 조회
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
