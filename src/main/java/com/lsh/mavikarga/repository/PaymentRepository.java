package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentInfo, Long> {
    PaymentInfo findByMerchantUid(String merchantUid);
}
