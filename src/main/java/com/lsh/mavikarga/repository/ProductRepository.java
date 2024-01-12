package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
