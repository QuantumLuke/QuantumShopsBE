package com.quantumluke.quantumshops.repository;

import com.quantumluke.quantumshops.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}