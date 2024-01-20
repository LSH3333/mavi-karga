package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySizes_id(Long productSizeId);

    // product.removed = false 인 product 들 찾음
    List<Product> findByRemovedFalse();
}
