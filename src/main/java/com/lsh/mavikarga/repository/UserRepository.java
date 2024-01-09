package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
