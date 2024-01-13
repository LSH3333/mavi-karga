package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.dto.ViewProductDto;
import com.lsh.mavikarga.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@Controller
@Slf4j
public class AdminController {

    private final ProductService productService;

    @Autowired
    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    // 상품 추가
    @GetMapping("/admins/products/add")
    public String addProductForm() {
        return "/admins/products/add";
    }

    @PostMapping("/admins/products/add")
    public String addProduct(@ModelAttribute AddProductDto addProductDto) {

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
