package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.dto.ViewProductDto;
import com.lsh.mavikarga.service.ProductImageService;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@Secured("ROLE_ADMIN")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final ProductImageService productImageService;

    @Autowired
    public AdminController(ProductService productService, UserService userService, ProductImageService productImageService) {
        this.productService = productService;
        this.userService = userService;
        this.productImageService = productImageService;
    }

    //////////////////// 상품 추가 ////////////////
    @GetMapping("/admins/products/add")
    public String addProductForm(@ModelAttribute("addProductDto") AddProductDto addProductDto) {
        return "admins/products/add";
    }

    // 상품 formData 담긴 ajax 요청 처리
    @PostMapping("/admins/products/add")
    public ResponseEntity<String> addProduct(@Valid @ModelAttribute("addProductDto") AddProductDto addProductDto, BindingResult bindingResult) {

        // 에러 있을시 에러메시지 body 에 포함해서 BAD_REQUEST 보냄
        if (bindingResult.hasErrors()) {
            // Extract field errors and build a response containing error messages
            StringBuilder errorMessageBuilder = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMessageBuilder.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append("\n");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageBuilder.toString());
        }

        // 에러 없으면 저장 처리
        Product product = productService.save(productService.createProductFromDto(addProductDto));

        // productId 바디에 담아서 리턴
        return ResponseEntity.status(HttpStatus.OK).body(product.getId().toString());
    }

    // 상품 이미지 업로드 ajax
    @PostMapping("/admins/products/images")
    public ResponseEntity<String> uploadAjax(
            @RequestParam UUID productId,
            @RequestPart(value = "multipartFiles", required = false) List<MultipartFile> files) throws IOException {

        // ProductImage 저장
        if(files != null) {
            productImageService.saveAllProductImages(files, productId);
        }

        return ResponseEntity.ok("/admins/products/view");
    }

    //////////////////// 상품 목록 ////////////////
    @GetMapping("/admins/products/view")
    public String listProductForm(Model model) {
        // product.removed=false 인 상품들만 가져옴
        List<Product> allProducts = productService.findNotRemovedProducts();
        List<ViewProductDto> viewProductDtoList = ViewProductDto.createViewProductDtoList(allProducts);
        model.addAttribute("products", viewProductDtoList);

        log.info("products = {}", viewProductDtoList);

        return "admins/products/view";
    }



    //////////////////// 상품 수정 ////////////////
    @GetMapping("/admins/products/edit/{productId}")
    public String editProductForm(Model model, @PathVariable UUID productId) {
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
    public String editProduct(@PathVariable UUID productId,
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


    //////////////////// 상품 제거 ////////////////
    @DeleteMapping("/admins/products/delete/{productId}")
    public String deleteProduct(@PathVariable UUID productId) {
        productService.makeProductRemovedTrue(productId);
        return "redirect:/admins/products/view";
    }




}
