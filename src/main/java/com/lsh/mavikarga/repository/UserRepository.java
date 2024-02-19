package com.lsh.mavikarga.repository;

import com.lsh.mavikarga.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByRole(String role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.lastLoggedIn < :sixMonthsAgo")
    List<User> findUsersWithLastLoggedInOlderThanSixMonths(@Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);
    
}
