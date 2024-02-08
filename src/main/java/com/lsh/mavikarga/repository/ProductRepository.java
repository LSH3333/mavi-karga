package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.enums.ClothingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySizes_id(Long productSizeId);

    // product.removed = false 인 product 들 찾음
    List<Product> findByRemovedFalse();

    // 관리자가 제외하지 않은 제품들중 카테고리로 찾음
    List<Product> findByClothingCategoryAndRemovedFalse(ClothingCategory clothingCategory);
}
