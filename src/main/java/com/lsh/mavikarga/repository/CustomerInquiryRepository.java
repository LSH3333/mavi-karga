package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.CustomerInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerInquiryRepository extends JpaRepository<CustomerInquiry, Long> {

    Page<CustomerInquiry> findAll(Pageable pageable);

}
