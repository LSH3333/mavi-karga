package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.dto.ViewProductDto;
import com.lsh.mavikarga.service.ProductService;
import com.lsh.mavikarga.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@Secured("ROLE_ADMIN")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public AdminController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    // 상품 추가
    @GetMapping("/admins/products/add")
    public String addProductForm(@ModelAttribute("addProductDto") AddProductDto addProductDto) {
        return "admins/products/add";
    }

    @PostMapping("/admins/products/add")
    public String addProduct(@Valid @ModelAttribute("addProductDto") AddProductDto addProductDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admins/products/add";
        }

        log.info("addProductDto = {}", addProductDto);

        Product product = productService.createProductFromDto(addProductDto);
        productService.save(product);

        return "redirect:/admins/products/add";
    }

    // 상품 목록
    @GetMapping("/admins/products/view")
    public String listProductForm(Model model) {
        // product.removed=false 인 상품들만 가져옴
        List<Product> allProducts = productService.findNotRemovedProducts();
        List<ViewProductDto> viewProductDtoList = ViewProductDto.createViewProductDtoList(allProducts);
        model.addAttribute("products", viewProductDtoList);

        log.info("products = {}", viewProductDtoList);

        return "admins/products/view";
    }

    // 상품 수정
    @GetMapping("/admins/products/edit/{productId}")
    public String editProductForm(Model model, @PathVariable Long productId) {
        Product product = productService.findById(productId).orElse(null);
        AddProductDto addProductDto = productService.createAddProductDto(productId);

        if (product == null || addProductDto == null) {
            return "error";
        }

        model.addAttribute("addProductDto", addProductDto);
        model.addAttribute("productId", productId);
        log.info("addProductDto = {}", addProductDto);
        return "admins/products/edit";
    }

    @PostMapping("/admins/products/edit/{productId}")
    public String editProduct(@PathVariable Long productId,
                              @Valid @ModelAttribute("addProductDto") AddProductDto addProductDto,
                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // 전체 URL을 지정하지 않고 뷰 이름을 반환하는 경우 Spring MVC는 리디렉션에 원래 요청의 URL 패턴을 사용함.
            // 이 동작은 view resolution, 리디렉션에 대한 Spring MVC의 기본 동작의 일부.
            // original request URL 은 '/admins/products/edit/{productId}' 이고
            // 'admins/products/edit' 을 리턴하면 Spring MVC는 원래 요청의 URL 패턴을 기반으로 리디렉션 URL을 구성.
            // URL에 {productId} 경로 변수가 자동으로 포함.
            return "admins/products/edit";
        }
        log.info("editProduct addProductDto = {}", addProductDto);
        // product 수정
        productService.updateWithAddProductDto(productId, addProductDto);

        return "redirect:/admins/products/view";
    }

    // 상품 제거
    @DeleteMapping("/admins/products/delete/{productId}")
    public String deleteProduct(@PathVariable Long productId) {
        productService.makeProductRemovedTrue(productId);
        return "redirect:/admins/products/view";
    }
}
