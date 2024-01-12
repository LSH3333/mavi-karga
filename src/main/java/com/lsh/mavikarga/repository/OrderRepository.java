package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderInfo, Long> {
}
