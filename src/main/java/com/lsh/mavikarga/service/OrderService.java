package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.*;
import com.lsh.mavikarga.dto.OrderProductDto;
import com.lsh.mavikarga.repository.*;
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
    private final CartRepository cartRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ProductRepository productRepository, ProductSizeRepository productSizeRepository,
                        CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productSizeRepository = productSizeRepository;
        this.cartRepository = cartRepository;
    }

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

    // 장바구니 추가
    public boolean addToCart(OrderProductDto orderProductDto, Long userId) {
        // 사용자가 선택한 ProductSize 를 기반으로 Product 객체 가져옴
        Product product = productRepository.findBySizes_id(orderProductDto.getSelectedProductSizeId()).orElse(null);
        ProductSize productSize = productSizeRepository.findById(orderProductDto.getSelectedProductSizeId()).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || productSize == null || product == null) {
            return false;
        }

        // 장바구니 없으면 새로 생성, 관계 연결
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cartRepository.save(cart);
            user.createCart(cart);
        }
        // 장바구니에 상품 추가
        cart.addProductSizeToCart(productSize);

        return true;
    }


}
