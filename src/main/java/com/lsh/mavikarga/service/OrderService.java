package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.OrderInfo;
import com.lsh.mavikarga.domain.OrderProduct;
import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.repository.OrderRepository;
import com.lsh.mavikarga.repository.ProductRepository;
import com.lsh.mavikarga.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

//    public Long order(Long userId, Long productId, int count) {
//        User user = userRepository.findById(userId).orElse(null);
//        Product product = productRepository.findById(productId).orElse(null);
//
//        // OrderProduct 생성
//        OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), count);
//
//        // Order 생성
//        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProduct);
//
//        orderRepository.save(orderInfo);
//        return orderInfo.getId();
//    }

    // Order 생성
    public Long order(Long userId, List<Long> productIdList, List<Integer> countList) {
        User user = userRepository.findById(userId).orElse(null);

        // OrderProduct 생성
        OrderProduct[] orderProducts = new OrderProduct[productIdList.size()];
        for(int i = 0; i < productIdList.size(); i++) {
            int count = countList.get(i);
            Product product = productRepository.findById(productIdList.get(i)).orElse(null);
            OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), count);
            orderProducts[i] = orderProduct;
        }

        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProducts);
        orderRepository.save(orderInfo);
        return orderInfo.getId();
    }
}
