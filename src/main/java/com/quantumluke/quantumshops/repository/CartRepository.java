package com.quantumluke.quantumshops.repository;

import com.quantumluke.quantumshops.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);
}