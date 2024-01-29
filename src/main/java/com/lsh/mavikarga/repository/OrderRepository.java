package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.OrderInfo;
import com.lsh.mavikarga.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderInfo, Long> {

    List<OrderInfo> findByUser(User user);
}
