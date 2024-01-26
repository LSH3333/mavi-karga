package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.ProductImage;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.CartProductDto;
import com.lsh.mavikarga.dto.CartProductDtoList;
import com.lsh.mavikarga.dto.OrderProductDto;
import com.lsh.mavikarga.service.OrderService;
import com.lsh.mavikarga.service.ProductImageService;
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
import java.util.UUID;

@Controller
@Slf4j
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final ProductImageService productImageService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, ProductService productService, ProductImageService productImageService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.productImageService = productImageService;
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
    public String productForm(Model model, @RequestParam UUID productId) {

        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            return "error";
        }
        // 상품 이미지
        // todo: 나중에 단일 상품 페이지 프론트 정확히 어떻게할것인지 듣고나서 결정
        List<String> allProductImagesUrlInProduct = productImageService.getAllProductImagesUrlInProduct(productId);


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


    // 장바구니 폼
    @GetMapping("/order/cart")
    public String cartForm(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName()).orElse(null);

        // 사용자 장바구니 담긴 상품들 보여주기
        CartProductDtoList cartProductDtoList = new CartProductDtoList(orderService.createCartProductDtoList(user.getId()));
        model.addAttribute("cartProductDtoList", cartProductDtoList);

        return "cart";
    }

    // 장바구니 폼에서 최종적으로 구매 결정 -> 구매 페이지로 이동
    @PostMapping("/order/cart")
    public String createOrder(@ModelAttribute CartProductDtoList cartProductDtoList) {

        log.info("CREATE ORDER");
        for (CartProductDto o : cartProductDtoList.getCartProductDtoList()) {
            log.info("id = {}", o.getCartId());
            log.info("getCount = {}", o.getCount());
            log.info("isDeleted = {}", o.isDeleted());
        }

        // 장바구니 수정 사항 업데이트
        orderService.updateCart(cartProductDtoList.getCartProductDtoList());



        return "redirect:/payments/payment";
    }



    @GetMapping("/test/productPageTest")
    public String productPageTest() {
        return "test/productPageTest";
    }
}
