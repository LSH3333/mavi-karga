package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.dto.AddProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class AdminController {

    @PostMapping("/admins/addProduct")
    public ResponseEntity<String> addProduct(@ModelAttribute AddProductDto addProductDto) {

        log.info("addProductDto = {}", addProductDto);

        // todo: 상품 저장 로직 

        return ResponseEntity.status(HttpStatus.OK).body("상품 추가 완료");
    }
}
