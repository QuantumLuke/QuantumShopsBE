package com.quantumluke.quantumshops.repository;

import com.quantumluke.quantumshops.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}