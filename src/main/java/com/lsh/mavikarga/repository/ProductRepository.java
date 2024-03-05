package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.enums.ClothingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByProductOptions_id(Long productSizeId);

    // product.removed = false 인 product 들 찾음
    // displayOrder 기준 오름차순 정렬
    List<Product> findByRemovedFalseOrderByDisplayOrderAsc();

    // 관리자가 제외하지 않은 제품들중 카테고리로 찾음
    // displayOrder 기준 오름차순 정렬
    List<Product> findByClothingCategoryAndRemovedFalseOrderByDisplayOrderAsc(ClothingCategory clothingCategory);


//    List<Product> findByMainProductAndRemovedFalse(boolean isMainProduct);

    // mainProduct, removed==false 로 상품 가져옴.
    // displayOrder 기준 오름차순 정렬
    List<Product> findByMainProductAndRemovedFalseOrderByDisplayOrderAsc(boolean mainProduct);
}
