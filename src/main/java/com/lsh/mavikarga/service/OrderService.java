package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.*;
import com.lsh.mavikarga.dto.CartProductDto;
import com.lsh.mavikarga.dto.OrderProductDto;
import com.lsh.mavikarga.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

//    public void createOrderInfo(OrderProductDto orderProductDto, Long userId) {
//        // 사용자가 선택한 ProductSize 를 기반으로 Product 객체 가져옴
//        Product product = productRepository.findBySizes_id(orderProductDto.getSelectedProductSizeId()).orElse(null);
//        ProductSize productSize = productSizeRepository.findById(orderProductDto.getSelectedProductSizeId()).orElse(null);
//        User user = userRepository.findById(userId).orElse(null);
//
//        log.info("product.getId() = {}", product.getId());
//
//        // OrderProduct 생성
//        OrderProduct orderProduct = OrderProduct.createOrderProduct(productSize, product.getPrice(), orderProductDto.getCount());
//        // Order 생성
//        OrderInfo orderInfo = OrderInfo.createOrderInfo(user, orderProduct);
//
//        orderRepository.save(orderInfo);
//    }

    // 장바구니 추가
    public boolean addCart(OrderProductDto orderProductDto, Long userId) {

        // 사용자가 선택한 ProductSize 를 기반으로 Product 객체 가져옴
        Product product = productRepository.findBySizes_id(orderProductDto.getSelectedProductSizeId()).orElse(null);
        ProductSize productSize = productSizeRepository.findById(orderProductDto.getSelectedProductSizeId()).orElse(null);
        // 사용자가 구매한 갯수
        int count = orderProductDto.getCount();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || productSize == null || product == null) {
            return false;
        }

        Cart cart = new Cart(productSize, count, user);
        cartRepository.save(cart);

        return true;
    }

    // 장바구니 조회폼 위한 CartProductDto 리스트 생성 후 리턴
    // 사용자가 보유중인 장바구니 조회
    public List<CartProductDto> createCartProductDtoList(Long userId) {
        List<CartProductDto> cartProductDtos = new ArrayList<>();
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return null;
        }

        List<Cart> carts = user.getCarts();
        for (Cart cart : carts) {
            ProductSize productSize = cart.getProductSize();
            Product product = productSize.getProduct();
            CartProductDto cartProductDto = new CartProductDto(cart.getId(), product.getName(), product.getPrice(), cart.getCount());
            cartProductDtos.add(cartProductDto);
        }
        return cartProductDtos;
    }

    // 장바구니 폼에서 보내온 정보 토대로 장바구니 업데이트 (상품 제거, 갯수 변경)
    public void updateCart(List<CartProductDto> cartProductDtoList) {

        for (CartProductDto cartProductDto : cartProductDtoList) {
            Cart cart = cartRepository.findById(cartProductDto.getCartId()).orElse(null);
            if(cart == null) continue;

            // 사용자가 해당 상품 장바구니에서 제외했다면 제거
            if (cartProductDto.isDeleted()) {
                cartRepository.delete(cart);
            }
            // 변경
            else {
                cart.setCount(cartProductDto.getCount());
            }
        }
    }
}
