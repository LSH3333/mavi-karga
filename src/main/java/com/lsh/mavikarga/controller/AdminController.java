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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

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
        return "/admins/products/add";
    }

    @PostMapping("/admins/products/add")
    public String addProduct(@Valid @ModelAttribute("addProductDto") AddProductDto addProductDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("BINDINGRESULT HAS ERROR");
            return "admins/products/add";
        }

        log.info("addProductDto = {}", addProductDto);

        Product product = new Product(addProductDto);
        productService.save(product);

        return "redirect:/admins/products/add";
    }

    // 상품 목록
    @GetMapping("/admins/products/view")
    public String listProductForm(Model model) {

        List<Product> allProducts = productService.findAll();
        List<ViewProductDto> viewProductDtoList = ViewProductDto.createViewProductDtoList(allProducts);
        model.addAttribute("products", viewProductDtoList);

        log.info("products = {}", viewProductDtoList);

        return "/admins/products/view";
    }

    // 상품 수정
    @PutMapping("/admins/products/{productId}")
    public String editProduct() {
        return "";
    }
}
