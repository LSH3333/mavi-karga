package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.ProductImage;
import com.lsh.mavikarga.repository.ProductImageRepository;
import com.lsh.mavikarga.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ProductImageService {

    private final S3Service s3Service;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductImageService(S3Service s3Service, ProductImageRepository productImageRepository, ProductRepository productRepository) {
        this.s3Service = s3Service;
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
    }

    // 단일 ProductImage 생성 후 저장
    private void saveProductImage(MultipartFile multipartFile, Product product, int thumbnail) throws IOException {
        String fileUrl = s3Service.upload(multipartFile, "images"); // S3 버킷의 images 디렉토리 안에 저장됨

        ProductImage productImage;
        // 썸네일 앞면
        if(thumbnail == 0) {
            productImage = new ProductImage(fileUrl, product, 1);
        }
        // 썸네일 뒷면
        else if(thumbnail == 1) {
            productImage = new ProductImage(fileUrl, product, 2);
        }
        // 썸네일 아님
        else {
            productImage = new ProductImage(fileUrl, product, 0);
        }

        productImageRepository.save(productImage);
    }

    // 제품의 이미지들 저장
    public void saveAllProductImages(List<MultipartFile> multipartFileList, UUID productId) throws IOException {
        Product product = productRepository.findById(productId).orElse(null);

        // 첫 두개의 사진을 썸네일 앞,뒷면으로 사용한다
        int thumbnail = 0;
        for (MultipartFile multipartFile : multipartFileList) {
            saveProductImage(multipartFile, product, thumbnail++);
        }
    }

    // 제품에 저장된 모든 제품 이미지 삭제
    public void deleteAllProductImages(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);

        List<ProductImage> productImages = product.getProductImages();
        if(productImages == null) return;

        for (ProductImage productImage : productImages) {
            productImage.setProduct(null);
        }
        product.getProductImages().clear();
    }

    // 상품의 모든 이미지들의 url 을 리스트로 만들어서 리턴
    public List<String> getAllProductImagesUrlInProduct(UUID productId) {
        List<String> productImagesUrlList = new ArrayList<>();
        Product product = productRepository.findById(productId).orElse(null);

        if(product != null) {
            List<ProductImage> productImages = product.getProductImages();
            for (ProductImage productImage : productImages) {
                productImagesUrlList.add(productImage.getUrl());
            }
            return productImagesUrlList;
        } else {
            return null;
        }
    }

}
