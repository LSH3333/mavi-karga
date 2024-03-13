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

        ProductImage productImage = new ProductImage(fileUrl, product);
        // 썸네일인 이미지라면 제품과 연관관계 맺어줌
        // 썸네일 앞면
        if(thumbnail == 0) {
            product.setThumbnail_front(productImage);
        }
        // 썸네일 뒷면
        else if(thumbnail == 1) {
            product.setThumbnail_back(productImage);
        }

        productImageRepository.save(productImage);
    }

    // 상품 썸네일 이미지 저장, 앞면이거나 뒷면
    private void saveProductImageThumbnail(Product product, boolean front) {

        String fileUrl = "https://mavikarga-bucket.s3.ap-northeast-2.amazonaws.com/images/empty_default.png";

        ProductImage productImage = new ProductImage(fileUrl, product);
        if(front) {
            product.setThumbnail_front(productImage);
        } else {
            product.setThumbnail_back(productImage);
        }

        productImageRepository.save(productImage);
    }

    // 제품의 이미지들 저장
    public void saveAllProductImages(List<MultipartFile> files, UUID productId) throws IOException {
//        log.info("saveAllProductImages = {}", productId);
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null) return;

        // 선택된 파일이 0개일경우 앞,뒤 썸네일 이미지 디폴트로 사용
        if(files == null) {
            saveProductImageThumbnail(product, true);
            saveProductImageThumbnail(product, false);
        }
        // 선택된 파일이 1개일 경우 뒤 썸네일 이미지 디폴트로 사용
        else if(files.size() == 1) {
            saveProductImage(files.get(0), product, 0);
            saveProductImageThumbnail(product, false);
        }
        // 첫번째 두번째 선택된 이미지를 각각 앞,뒤 썸네일 이미지로 사용
        else {
            // 첫 두개의 사진을 썸네일 앞,뒷면으로 사용한다
            int thumbnail = 0;
            for (MultipartFile multipartFile : files) {
                saveProductImage(multipartFile, product, thumbnail++);
            }
        }

    }

    // 제품에 저장된 모든 제품 이미지 삭제
    public void deleteAllProductImages(UUID productId) {

        Product product = productRepository.findById(productId).orElse(null);
        if(product == null) return;

        List<ProductImage> productImages = product.getProductImages();
        if(productImages == null) return;

        // 이미지 삭제, Product 와 ProductImage 연관관계 제거
        product.getProductImages().clear();
        for (ProductImage productImage : productImages) {
            productImage.setProduct(null);
        }

        // 썸네일 이미지 삭제
        if(product.getThumbnail_front() != null) {
            product.getThumbnail_front().setProduct(null);
            product.setThumbnail_front(null);
        }
        if (product.getThumbnail_back() != null) {
            product.getThumbnail_back().setProduct(null);
            product.setThumbnail_back(null);
        }


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
