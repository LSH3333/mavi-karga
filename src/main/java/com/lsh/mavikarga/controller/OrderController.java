package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.CartForNonUser;
import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.*;
import com.lsh.mavikarga.enums.ClothingCategory;
import com.lsh.mavikarga.service.OrderService;
import com.lsh.mavikarga.service.ProductImageService;
import com.lsh.mavikarga.service.ProductService;
import com.lsh.mavikarga.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.security.Principal;
import java.util.*;

@Controller
@Slf4j
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final ProductImageService productImageService;
    private final LocaleResolver localeResolver;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, ProductService productService,
                           ProductImageService productImageService, LocaleResolver localeResolver) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
        this.productImageService = productImageService;
        this.localeResolver = localeResolver;
    }


    /////////////////////////////// 전체 상품 페이지 ///////////////////////////////
    @GetMapping("/clothing")
    public String clothingMain(Model model, HttpServletRequest request) {
        // 카테고리가 메인인 상품들
        List<Product> products = productService.findByClothingCategoryAndRemovedFalse(ClothingCategory.MAIN);
        ClothingDtoList clothingDtoList = productService.createClothingDto(products, request);

        model.addAttribute("clothingDtoList", clothingDtoList);
        // 헤더 navBar 선택한 탭 강조 처리용
        model.addAttribute("menu", "clothing");
        return "clothing";
    }

    @GetMapping("/clothing/all")
    public String clothingCategoryAll(Model model, HttpServletRequest request) {
        // 카테고리 무관 전체 상품
        List<Product> products = productService.findNotRemovedProducts();
        ClothingDtoList clothingDtoList = productService.createClothingDto(products, request);

        model.addAttribute("clothingDtoList", clothingDtoList);
        model.addAttribute("menu", "all");
        return "clothing/all";
    }

    @GetMapping("/clothing/one")
    public String clothingCategoryOne(Model model, HttpServletRequest request) {
        // 카테고리가 ONE 인 상품들
        List<Product> products = productService.findByClothingCategoryAndRemovedFalse(ClothingCategory.ONE);
        ClothingDtoList clothingDtoList = productService.createClothingDto(products, request);

        model.addAttribute("clothingDtoList", clothingDtoList);
        model.addAttribute("menu", "one");
        return "clothing/one";
    }

    @GetMapping("/clothing/two")
    public String clothingCategoryTwo(Model model, HttpServletRequest request) {
        // 카테고리가 TWO 인 상품들
        List<Product> products = productService.findByClothingCategoryAndRemovedFalse(ClothingCategory.TWO);
        ClothingDtoList clothingDtoList = productService.createClothingDto(products, request);

        model.addAttribute("clothingDtoList", clothingDtoList);
        model.addAttribute("menu", "two");
        return "clothing/two";
    }

    /////////////////////////////// 단일 상품 페이지 ///////////////////////////////
    @GetMapping("/order/products")
    public String productForm(Model model, @RequestParam UUID productId, HttpServletResponse response, HttpServletRequest request) {

        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            return "error";
        }

        // 상품 이미지
        List<String> allProductImagesUrlInProduct = productImageService.getAllProductImagesUrlInProduct(productId);

        // 클라이언트로 보낼 DTO
        OrderProductDto orderProductDto = new OrderProductDto(product.getName(), product.getDescription(), product.getProductOptions(),
                allProductImagesUrlInProduct, getPriceByLocale(response, request, product));

        // 사이즈 정렬 (S,M,L ... )
        Collections.sort(orderProductDto.getProductSizeList());

        model.addAttribute("orderProductDto", orderProductDto);

        return "productPage";
    }

    // 현재 Locale 확인해서 그에 맞는 상품 가격 리턴
    private int getPriceByLocale(HttpServletResponse response, HttpServletRequest request, Product product) {
        Locale currentLocale = localeResolver.resolveLocale(request);
        if(currentLocale == Locale.US) {
            return product.getPrice_USD();
        } else {
            return product.getPrice();
        }
    }

    // offcanvs 장바구니 데이터 ajax 요청 (랜더링용)
    @GetMapping("/order/products/cart")
    public ResponseEntity<List<CartProductDto>> offCanvasCartRequest(HttpSession session, Principal principal) {

        List<CartProductDto> cartProductDtoList;

        // 비회원
        if(principal == null) {
            // 사용자 장바구니 담긴 상품들 보여줄 dto
            // 비회원 세션 기반으로 장바구니 dto 에 정보 담음
            cartProductDtoList = orderService.createCartProductDtoListForNonUser(session);
        }
        // 회원
        else {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if(user == null) {
                return ResponseEntity.badRequest().build();
            }
            cartProductDtoList = orderService.createCartProductDtoList(user.getId());
        }

        return ResponseEntity.ok(cartProductDtoList);
    }

    // offcanvas 장바구니 물품 제거 ajax 요청
    @PostMapping("/order/products/cart/remove")
    public ResponseEntity<String> offCanvasCartRemoveRequest(Principal principal, HttpSession session, @RequestParam String cartId) {

        log.info("offCanvasCartRemoveRequest = {}", cartId);

        boolean result;

        // 비회원
        if(principal == null) {
            result = orderService.removeCartNonUser(Integer.parseInt(cartId), session);
        }
        // 회원
        else {
            result = orderService.removeCart(Long.parseLong(cartId));
        }

        if(result) {
            return ResponseEntity.status(HttpStatus.OK).body("success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }
    }

    // offcanvas 상품 갯수 수정 ajax 요청
    @PostMapping("/order/products/cart/count")
    public ResponseEntity<String> offCanvasCartProductCountChangeRequest(Principal principal, HttpSession session, @RequestParam String cartId, @RequestParam int count) {
        boolean result;

        // 비회원
        if(principal == null) {
            result = orderService.changeProductCountCartNonUser(Integer.parseInt(cartId), session, count);
        }
        // 회원
        else {
            result = orderService.changeProductCountCart(Long.parseLong(cartId), count);
        }

        if(result) {
            return ResponseEntity.status(HttpStatus.OK).body("success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }
    }

    // offcanvas checkout 버튼 클릭, 구매 페이지로 이동
    @GetMapping("/order/products/cart/checkout")
    public String offCanvasProceedToCheckout(Principal principal) {
        if(principal == null) {
            return "redirect:/payments/payment/nonuser";
        } else {
            return "redirect:/payments/payment";
        }
    }

    /////////////////////////////// 장바구니 ///////////////////////////////

    //  you can't redirect user in ajax requests!
    // 회원 장바구니 추가 ajax
    @PostMapping("/order/products/add")
    public ResponseEntity<String> addProductToOrder(
            @ModelAttribute OrderProductDto orderProductDto,
            Principal principal) {

        log.info("orderProductDto.getSelectedProductSizeId() = {}", orderProductDto.getSelectedProductSizeId());
        log.info("orderProductDto.count() = {}", orderProductDto.getCount());

        User user = userService.findByUsername(principal.getName()).orElse(null);
        // user 세션에 문제 있거나, 사이즈 선택 안했을 경우 BAD_REQUEST
        if (user == null || orderProductDto.getSelectedProductSizeId() == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");
        }

        // 장바구니 추가
        if (!orderService.addCart(orderProductDto, user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }


        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    // 비회원 장바구니 추가 ajax
    @PostMapping("/order/products/add/nonuser")
    public ResponseEntity<String> addProductToOrderNonUser(HttpSession session, @ModelAttribute OrderProductDto orderProductDto) {
        log.info("addProductToOrderNonUser");
        // 사이즈 선택 안했을 경우 BAD_REQUEST
        if (orderProductDto.getSelectedProductSizeId() == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");
        }

        // 세션에서 장바구니 가져옴
        List<CartForNonUser> cartList = (List<CartForNonUser>) session.getAttribute("cart");
        // 세션에 장바구니 없으면 새로 만듦
        if (cartList == null) {
            cartList = new ArrayList<CartForNonUser>();
            session.setAttribute("cart", cartList);
        }


        // 장바구니에 상품, 갯수 추가
        if (!orderService.addCartForNonUser(orderProductDto, cartList, session)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    // 회원 장바구니 폼
    @GetMapping("/order/cart")
    public String cartForm(Principal principal, HttpSession session, Model model) {

        // 사용자 장바구니 담긴 상품들 보여주기
        CartProductDtoList cartProductDtoList;

        User user = userService.findByUsername(principal.getName()).orElse(null);
        cartProductDtoList = new CartProductDtoList(orderService.createCartProductDtoList(user.getId()));

        model.addAttribute("cartProductDtoList", cartProductDtoList);

        return "cart";
    }

    // 비회원 장바구니 폼
    @GetMapping("/order/cart/nonuser")
    public String cartFormNonUser(HttpSession session, Model model, Principal principal) {
        // 로그인된 상태로 비회원 장바구니 폼 접근 시도 시 홈으로 리다이렉트
        if(principal != null) {
            return "redirect:/";
        }

        // 사용자 장바구니 담긴 상품들 보여줄 dto
        CartProductDtoList cartProductDtoList;
        // 비회원 세션 기반으로 장바구니 dto 에 정보 담음
        cartProductDtoList = new CartProductDtoList(orderService.createCartProductDtoListForNonUser(session));

        model.addAttribute("cartProductDtoList", cartProductDtoList);

        return "cartNonUser";
    }

    // 회원 장바구니 폼에서 최종적으로 구매 결정 -> 구매 페이지로 이동
    @PostMapping("/order/cart")
    public String createOrder(@ModelAttribute CartProductDtoList cartProductDtoList, Principal principal, HttpSession session) {
        // 장바구니 수정 사항 업데이트
//        orderService.updateCart(cartProductDtoList.getCartProductDtoList());
        return "redirect:/payments/payment";
    }

    // 회원 장바구니 상품 제거 요청 ajax
    @PostMapping("/order/cart/remove")
    public ResponseEntity<String> removeCart(@RequestParam Long cartId) {
        if (orderService.removeCart(cartId)) {
            return ResponseEntity.status(HttpStatus.OK).body("success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }
    }

    // 비회원 장바구니 폼에서 최종적으로 구매 결정 -> 구매 페이지로 이동
    @PostMapping("/order/cart/nonuser")
    public String createOrderNonUser(@ModelAttribute CartProductDtoList cartProductDtoList, HttpSession session) {
//        orderService.updateCartNonUser(cartProductDtoList.getCartProductDtoList(), session);
        return "redirect:/payments/payment/nonuser";
    }

    // 비회원 장바구니 상품 제거 요청 ajax
    @PostMapping("/order/cart/nonuser/remove")
    public ResponseEntity<String> removeCartNonUser(HttpSession session, @RequestParam int cartId) {
        if (orderService.removeCartNonUser(cartId, session)) {
            return ResponseEntity.status(HttpStatus.OK).body("success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }
    }


    //////////////////// 주문번호로 주문 조회 ////////////////////

    @GetMapping("/order/lookup")
    public String findOrderWithOrderLookUpNumberForm() {
        return "FindOrderWithOrderLookUpNumber";
    }

    // 주문 조회 번호로 주문 조회 ajax
    @GetMapping("/order/lookupSearch")
    public ResponseEntity<List<MyPageDto>> findOrderWithOrderLookUpNumber(@RequestParam String orderLookUpNumber) {
        List<MyPageDto> myPageDtoList = orderService.findOrderWithOrderLookUpNumber(orderLookUpNumber);
        if(myPageDtoList == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(myPageDtoList);
    }

}
