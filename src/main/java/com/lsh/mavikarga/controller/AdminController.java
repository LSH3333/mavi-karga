package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class AdminController {

    private final ProductService productService;

    @Autowired
    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 추가
    @PostMapping("/admins/addProduct")
    public String addProduct(@ModelAttribute AddProductDto addProductDto) {

        log.info("addProductDto = {}", addProductDto);

        // todo: 상품 저장 로직
        Product product = new Product(addProductDto);
        productService.save(product);

        return "admins/addProduct";
    }
}
