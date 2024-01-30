package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.OrderInfo;
import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderInfo, Long> {

    List<OrderInfo> findByUser(User user);

    Page<OrderInfo> findAllByOrderStatus(OrderStatus orderStatus, Pageable pageable);
}
