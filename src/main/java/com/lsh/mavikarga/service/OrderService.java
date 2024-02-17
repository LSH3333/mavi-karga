package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.*;
import com.lsh.mavikarga.dto.CartProductDto;
import com.lsh.mavikarga.dto.MyPageDto;
import com.lsh.mavikarga.dto.MyPageDtoList;
import com.lsh.mavikarga.dto.OrderProductDto;
import com.lsh.mavikarga.dto.admin.showUserOrderToAdmin.ShowUserOrderToAdminDto;
import com.lsh.mavikarga.dto.admin.showUserOrderToAdmin.ShowUserOrderToAdminDtoList;
import com.lsh.mavikarga.dto.admin.showUserOrderToAdmin.ShowUserOrderToAdminOrderProductDto;
import com.lsh.mavikarga.enums.OrderStatus;
import com.lsh.mavikarga.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
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


    //////////////////////////// 회원 장바구니 ////////////////////////////
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
        if (user == null) {
            return null;
        }

        List<Cart> carts = user.getCarts();
        for (Cart cart : carts) {
            ProductSize productSize = cart.getProductSize();
            Product product = productSize.getProduct();
            // 기본 썸네일
            String thumbnail_url = "https://mavikarga-bucket.s3.ap-northeast-2.amazonaws.com/images/thumbnail_front_default.jpg";
            if(product.getThumbnail_front() != null) {
                thumbnail_url = product.getThumbnail_front().getUrl();
            }
            CartProductDto cartProductDto = new CartProductDto(cart.getId(), product.getId(), product.getName(), product.getPrice(),
                    cart.getCount(), thumbnail_url);
            cartProductDtos.add(cartProductDto);
        }
        return cartProductDtos;
    }

    // 장바구니 폼에서 보내온 정보 토대로 장바구니 업데이트 (상품 제거, 갯수 변경)
    public void updateCart(List<CartProductDto> cartProductDtoList) {

        for (CartProductDto cartProductDto : cartProductDtoList) {
            Cart cart = cartRepository.findById(cartProductDto.getCartId()).orElse(null);
            if (cart == null) continue;
            cart.setCount(cartProductDto.getCount());
        }
    }

    // 회원 장바구니 상품 제거
    public boolean removeCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if(cart == null) return false;
        cartRepository.delete(cart);
        return true;
    }

    //////////////////////////// 비회원 장바구니 ////////////////////////////
    // 비회원 장바구니 추가
    // 비회원의 세션 어트리뷰트 키 "cartList" 에 비회원 장바구니 객체 추가함
    public boolean addCartForNonUser(OrderProductDto orderProductDto, List<CartForNonUser> cartList, HttpSession session) {

        // 사용자가 선택한 ProductSize 를 기반으로 Product 객체 가져옴
        Product product = productRepository.findBySizes_id(orderProductDto.getSelectedProductSizeId()).orElse(null);
        ProductSize productSize = productSizeRepository.findById(orderProductDto.getSelectedProductSizeId()).orElse(null);
        // 사용자가 구매한 갯수
        int count = orderProductDto.getCount();

        if (productSize == null || product == null) {
            return false;
        }

        // 비회원 장바구니에 추가
        CartForNonUser cartForNonUser = new CartForNonUser(cartList.size(), productSize, count);
        cartList.add(cartForNonUser);

        return true;
    }

    // 장바구니 조회폼 위한 CartProductDto 리스트 생성 후 리턴
    // 비회원이 세션에 보유중인 장바구니 조회
    public List<CartProductDto> createCartProductDtoListForNonUser(HttpSession session) {
        List<CartProductDto> cartProductDtos = new ArrayList<>();

        // 세션에서 장바구니 가져옴
        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");
        // 세션에 장바구니 없으면 새로 만듦
        if (cartList == null) {
            cartList = new ArrayList<>();
            session.setAttribute("cart", cartList);
        }

        for (CartForNonUser cartForNonUser : cartList) {
            ProductSize productSize = cartForNonUser.getProductSize();
            Product product = productSize.getProduct();
            String thumbnail_url = "https://mavikarga-bucket.s3.ap-northeast-2.amazonaws.com/images/thumbnail_front_default.jpg";
            if(product.getThumbnail_front() != null) {
                thumbnail_url = product.getThumbnail_front().getUrl();
            }
            CartProductDto cartProductDto = new CartProductDto((long) cartForNonUser.getId(), product.getId(), product.getName(), product.getPrice(),
                    cartForNonUser.getCount(), thumbnail_url);
            cartProductDtos.add(cartProductDto);
        }

        return cartProductDtos;
    }

    // 장바구니 폼에서 보내온 정보 토대로 장바구니 업데이트 (상품 제거, 갯수 변경)
    /**
     * @param cartProductDtoList: 클라이언트에서 보내온 dto, 갯수 변경 정보 담겨있음
     */
    public void updateCartNonUser(List<CartProductDto> cartProductDtoList, HttpSession session) {
        // 세션에서 장바구니 가져옴
        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");

        // DTO 를 기반으로 비회원의 세션에 저장되어 있는 cartList 수정함
        for (CartProductDto cartProductDto : cartProductDtoList) {
            int cartId = cartProductDto.getCartId().intValue();
            CartForNonUser cartForNonUser = findCartFromSession(cartId, cartList);
            if(cartForNonUser == null) continue;
            cartForNonUser.setCount(cartProductDto.getCount());
        }
    }
    // 세션의 비회원 카트에서 cartId 에 해당하는 카트 찾음
    private CartForNonUser findCartFromSession(int cartId, List<CartForNonUser> cartList) {
        for (CartForNonUser cart : cartList) {
            if (cart.getId() == cartId) {
                return cart;
            }
        }
        return null;
    }

    // 비회원 장바구니 상품 제거
    public boolean removeCartNonUser(int cartId, HttpSession session) {
        // 세션에서 장바구니 가져옴
        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");
        if(cartList == null || cartId >= cartList.size()) return false;
        cartList.remove(cartId);
        return true;
    }

    //////////////////////////// 관리자 콘솔에서 사용자의 주문 목록 보는 뷰
    // 사용자의 주문 목록 찾음
    public List<OrderInfo> findByUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return orderRepository.findByUser(user);
    }

    // 사용자의 주문 목록 가져와서 클라이언트로 보내기 위한 ShowUserOrderToAdminDtoList 만듦
    public ShowUserOrderToAdminDtoList createShowUserOrderToAdminDtoList(Long userId, int page, int size, String orderStatus) {
        // 사용자
        User user = userRepository.findById(userId).orElse(null);

        // 사용자의 주문 목록.
        Page<OrderInfo> orderInfoList;
        // 주문일자 기준 오름차순으로 가져옴
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").ascending());
        // 모든, 배송 미완료, 배송 완료
        if (orderStatus.equals("ALL")) {
            orderInfoList = orderRepository.findByUser(user, pageable);
        } else if (orderStatus.equals(OrderStatus.NOT_DONE.toString())) {
            orderInfoList = orderRepository.findByUserAndOrderStatus(user, OrderStatus.NOT_DONE, pageable);
        } else {
            orderInfoList = orderRepository.findByUserAndOrderStatus(user, OrderStatus.DONE, pageable);
        }

        // DTO
        ShowUserOrderToAdminDtoList showUserOrderToAdminDtoList = createShowUserOrderToAdminDtoList(orderInfoList);
        showUserOrderToAdminDtoList.setTotalPages(orderInfoList.getTotalPages());

        return showUserOrderToAdminDtoList;
    }


    ///////// 주문 목록
    // 관리자 콘솔, 주문목록을 위한 DTO 생성
    public ShowUserOrderToAdminDtoList createDtoForAdminOrdersView(int page, int size, String orderStatus) {

        Page<OrderInfo> orderInfoList;
        // 주문일자 기준 오름차순으로 가져옴
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").ascending());

        // 모든, 배송 미완료, 배송 완료
        if (orderStatus.equals("ALL")) {
            // 모두 가져옴
            orderInfoList = orderRepository.findAll(pageable);
        } else if (orderStatus.equals(OrderStatus.NOT_DONE.toString())) {
            // 배송 완료 되지 않은 주문만 가져옴 (orderStatus==OrderStatus.NOT_DONE)
            orderInfoList = orderRepository.findAllByOrderStatus(OrderStatus.NOT_DONE, pageable);
        } else {
            // 배송 완료 된 주문만 가져옴 (orderStatus==OrderStatus.DONE)
            orderInfoList = orderRepository.findAllByOrderStatus(OrderStatus.DONE, pageable);
        }

        // Dto
        ShowUserOrderToAdminDtoList showUserOrderToAdminDtoList = createShowUserOrderToAdminDtoList(orderInfoList);
        showUserOrderToAdminDtoList.setTotalPages(orderInfoList.getTotalPages());

        return showUserOrderToAdminDtoList;
    }

    // ShowUserOrderToAdminDtoList 생성
    private ShowUserOrderToAdminDtoList createShowUserOrderToAdminDtoList(Page<OrderInfo> orderInfoList) {
        // 클라이언트로 보낼 DTO
        ShowUserOrderToAdminDtoList showUserOrderToAdminDtoList = new ShowUserOrderToAdminDtoList();

        // 사용자의 주문 목록 순회하면서 ShowUserOrderToAdminDto, ShowUserOrderToAdminOrderProductDto 생성
        for (OrderInfo order : orderInfoList) {
            // ShowUserOrderToAdminDto
            ShowUserOrderToAdminDto showUserOrderToAdminDto = new ShowUserOrderToAdminDto();

            // 주문정보 ID, 주문일자
            showUserOrderToAdminDto.setOrderInfoId(order.getId());
            showUserOrderToAdminDto.setOrderDate(order.getOrderDate());
            // 배송 정보
            Delivery delivery = order.getDelivery();
            showUserOrderToAdminDto.setDelivery(delivery);
            // 처리 상태
            showUserOrderToAdminDto.setOrderStatus(order.getOrderStatus());

            // showUserOrderToAdminOrderProductDtoList
            List<OrderProduct> orderProducts = order.getOrderProducts();
            for (OrderProduct orderProduct : orderProducts) {
                ShowUserOrderToAdminOrderProductDto showUserOrderToAdminOrderProductDto = new ShowUserOrderToAdminOrderProductDto();

                showUserOrderToAdminOrderProductDto.setOrderPrice(orderProduct.getOrderPrice());
                showUserOrderToAdminOrderProductDto.setCount(orderProduct.getCount());
                showUserOrderToAdminOrderProductDto.setSize(orderProduct.getProductSize().getSize());
                showUserOrderToAdminOrderProductDto.setProductId(orderProduct.getProductSize().getProduct().getId());
                showUserOrderToAdminOrderProductDto.setName(orderProduct.getProductSize().getProduct().getName());

                showUserOrderToAdminDto.getShowUserOrderToAdminOrderProductDtoList().add(showUserOrderToAdminOrderProductDto);
            }

            showUserOrderToAdminDtoList.getShowUserOrderToAdminDtoList().add(showUserOrderToAdminDto);
        }

        return showUserOrderToAdminDtoList;
    }

    // 주문정보의 처리상태 변경
    public void changeOrderStatus(Long orderInfoId, String status) {
        OrderInfo orderInfo = orderRepository.findById(orderInfoId).orElse(null);
        if (orderInfo == null) return;
        OrderStatus orderStatus;

        if (status.equals(OrderStatus.NOT_DONE.toString())) {
            orderStatus = OrderStatus.NOT_DONE;
        } else {
            orderStatus = OrderStatus.DONE;
        }

        orderInfo.setOrderStatus(orderStatus);
    }


    /////////// 마이페이지
    // 마이페이지에서 보여줄 사용자의 주문 목록 렌더링 위한 MyPageDtoList 생성 
    public MyPageDtoList createMyPageDtoList(Long userId, int page, int size) {
        // List Dto
        MyPageDtoList myPageDtoList = new MyPageDtoList();
        // 사용자
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return myPageDtoList;

        Page<OrderInfo> orderList = orderRepository.findByUser(user, PageRequest.of(page, size, Sort.by("orderDate").ascending()));
        // 주문정보 순회
        for (OrderInfo orderInfo : orderList) {
            // 주문정보에 속한 주문제품 순회
            for (OrderProduct orderProduct : orderInfo.getOrderProducts()) {
                // Dto
                MyPageDto myPageDto = new MyPageDto();

                // 상품명
                myPageDto.setName(orderProduct.getProductSize().getProduct().getName());
                // 주문 일자
                myPageDto.setOrderDate(orderInfo.getOrderDate());
                // 구매한 상품 개당 가격
                myPageDto.setOrderPrice(orderProduct.getOrderPrice());
                // 구매한 갯수
                myPageDto.setCount(orderProduct.getCount());
                // 처리 상태
                myPageDto.setOrderStatus(orderInfo.getOrderStatus());

                // 리스트에 추가
                myPageDtoList.getMyPageDtoList().add(myPageDto);
            }
        }

        myPageDtoList.setTotalPages(orderList.getTotalPages());
        return myPageDtoList;
    }



    // 비회원용 주문번호로 주문 조회
    public List<MyPageDto> findOrderWithOrderLookUpNumber(String orderLookUpNumber) {
        List<MyPageDto> myPageDtoList = new ArrayList<>();

        OrderInfo orderInfo = orderRepository.findByOrderLookUpNumber(orderLookUpNumber);

        // 주문번호가 잘못됨
        if(orderInfo == null) {
            return null;
        }
        List<OrderProduct> orderProducts = orderInfo.getOrderProducts();

        for (OrderProduct orderProduct : orderProducts) {
            log.info("orderProduct = {}", orderProduct.getOrderPrice());
            Product product = orderProduct.getProductSize().getProduct();
            // Dto
            MyPageDto myPageDto = new MyPageDto();
            // 상품 ID
            myPageDto.setProductId(product.getId());
            // 상품명
            myPageDto.setName(product.getName());
            // 주문 일자
            myPageDto.setOrderDate(orderInfo.getOrderDate());
            // 구매한 상품 개당 가격
            myPageDto.setOrderPrice(orderProduct.getOrderPrice());
            // 구매한 갯수
            myPageDto.setCount(orderProduct.getCount());
            // 처리 상태
            myPageDto.setOrderStatus(orderInfo.getOrderStatus());
            // 썸네일 이미지 url
            String thumbnail_url = "";
            if(product.getThumbnail_front() != null) {
                thumbnail_url = product.getThumbnail_front().getUrl();
            }
            myPageDto.setThumbnail_url(thumbnail_url);
            // 주문 조회 번호
            myPageDto.setOrderLookUpNumber(orderLookUpNumber);

            // 리스트에 추가
            myPageDtoList.add(myPageDto);
        }

        return myPageDtoList;
    }
}
