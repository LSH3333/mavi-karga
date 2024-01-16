package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.*;
import com.lsh.mavikarga.dto.OrderProductDto;
import com.lsh.mavikarga.repository.OrderRepository;
import com.lsh.mavikarga.repository.ProductRepository;
import com.lsh.mavikarga.repository.ProductSizeRepository;
import com.lsh.mavikarga.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, ProductSizeRepository productSizeRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productSizeRepository = productSizeRepository;
    }

    /**
     * 1개 이상의 Product의 OrderInfo 생성
     * @param userId: 구매한 User id
     * @param productIdList: 구매하는 물품의 id 리스트
     * @param countList: 구매하는 물품의 갯수 리스트
     * @return orderInfo.getId(): 생성된 OrderInfo 의 id
     */
//    public Long order(Long userId, List<Long> productIdList, List<Integer> countList) {
//        User user = userRepository.findById(userId).orElse(null);
//
//        // OrderProduct 생성
//        OrderProduct[] orderProducts = new OrderProduct[productIdList.size()];
//        for(int i = 0; i < productIdList.size(); i++) {
//            int count = countList.get(i);
//            Product product = productRepository.findById(productIdList.get(i)).orElse(null);
//            OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), count);
//            orderProducts[i] = orderProduct;
//        }
//
//        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProducts);
//        orderRepository.save(orderInfo);
//        return orderInfo.getId();
//    }
//
//    // 단건 Product 의 OrderInfo 생성
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

    public void createOrderInfo(OrderProductDto orderProductDto, Long userId) {
        // 사용자가 선택한 ProductSize 를 기반으로 Product 객체 가져옴
        Product product = productRepository.findBySizes_id(orderProductDto.getSelectedProductSizeId()).orElse(null);
        ProductSize productSize = productSizeRepository.findById(orderProductDto.getSelectedProductSizeId()).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        log.info("product.getId() = {}", product.getId());

        // OrderProduct 생성
        OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, product.getPrice(), orderProductDto.getCount());
        // Order 생성
        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProduct);

        orderRepository.save(orderInfo);
    }
}
