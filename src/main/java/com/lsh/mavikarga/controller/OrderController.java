package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.CartProductDto;
import com.lsh.mavikarga.dto.OrderProductDto;
import com.lsh.mavikarga.service.OrderService;
import com.lsh.mavikarga.service.ProductService;
import com.lsh.mavikarga.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, ProductService productService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    // 전체 상품 페이지
    @GetMapping("/clothing")
    public String products(Model model) {
        // 전체 상품(product.removed=false 인 상품) 가져와서 뿌린다
        List<Product> products = productService.findNotRemovedProducts();
        model.addAttribute("products", products);
        model.addAttribute("menu", "clothing");
        return "clothing";
    }

    // 단일 상품 페이지
    @GetMapping("/order/products")
    public String productForm(Model model, @RequestParam String productId) {
        log.info("productForm");

        Product product = productService.findById(Long.parseLong(productId)).orElse(null);
        if (product == null) {
            return "error";
        }

        OrderProductDto orderProductDto = new OrderProductDto(product.getName(), product.getDescription(), product.getSizes());

        model.addAttribute("orderProductDto", orderProductDto);

        return "productPage";
    }


    //  you can't redirect user in ajax requests!
    // 장바구니 추가
    @PostMapping("/order/products/add")
    public ResponseEntity<String> addProductToOrder(
            @ModelAttribute OrderProductDto orderProductDto,
            Principal principal) {

        log.info("orderProductDto.getSelectedProductSizeId() = {}", orderProductDto.getSelectedProductSizeId());
        log.info("orderProductDto.count() = {}", orderProductDto.getCount());

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");
        }

        // 장바구니 추가
        if (!orderService.addCart(orderProductDto, user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }


        return ResponseEntity.status(HttpStatus.OK).body("success");
    }


    // todo: CartProductDto 를 리스트로 갖는 DTO 만들어서 클라이언트에 보내기. 그리고 post 로 받기.
    // 장바구니 폼
    @GetMapping("/order/cart")
    public String cartForm(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName()).orElse(null);

        // 사용자 장바구니 담긴 상품들 보여주기
        List<CartProductDto> cartProductDtoList = orderService.createCartProductDtoList(user.getId());
        model.addAttribute("cartProductDtoList", cartProductDtoList);


        // 사용자가 최종 결정한 내용 (장바구니 페이지에서 골랐던 제품 제거할수도 있고 갯수 변경할수도 있음)


        return "cart";
    }

    @PostMapping("/order/cart")
    public String cart(@ModelAttribute("cartProductDtoList") List<CartProductDto> cartProductDtoList) {

        return "index";
    }

}
