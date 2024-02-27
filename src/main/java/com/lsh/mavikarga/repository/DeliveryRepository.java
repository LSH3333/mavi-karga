package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByMerchantUid(String merchantUid);

}
