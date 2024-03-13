package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class S3Controller {

    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/s3upload")
    public String uploadForm() {
        return "test/uploadTest";
    }

    @GetMapping("/s3render")
    public String s3renderTest(Model model) {
        String s3ImgUrl = s3Service.getS3url("images", "baekjoon.png");
        log.info("s3ImgUrl = {}", s3ImgUrl);
        model.addAttribute("s3ImgUrl", s3ImgUrl);
        return "test/s3renderTest";
    }

    /**
     * 생성
     */
    @PostMapping("/s3upload")
    public ResponseEntity<String> create(
            @RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile) {
        String fileName = "";
        if (multipartFile != null) { // 파일 업로드한 경우에만
            try {// 파일 업로드
                fileName = s3Service.upload(multipartFile, "images"); // S3 버킷의 images 디렉토리 안에 저장됨
                log.info("filename = {}", fileName); // filename = https://mavikarga-bucket.s3.ap-northeast-2.amazonaws.com/images/Official_unity_logo.png
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("file upload FAILED to AWS S3");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("file upload SUCCESS to AWS S3");
    }

    @PostMapping("/s3uploadMany")
    public ResponseEntity<String> createMany(@RequestPart(value = "multipartFiles", required = false) MultipartFile[] multipartFiles) {
        // Process each file in the files array
        for (MultipartFile file : multipartFiles) {
//            log.info("file = {}", file.getOriginalFilename());
            // todo: productImageService 로 이미지 db 에 저장
        }

        // Return a response as needed
        return ResponseEntity.ok("Files uploaded successfully");
    }


}
