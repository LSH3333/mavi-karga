package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

//    Optional<Cart> findByUser_id(Long userId);

}
