package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByRole(String role, Pageable pageable);
}
