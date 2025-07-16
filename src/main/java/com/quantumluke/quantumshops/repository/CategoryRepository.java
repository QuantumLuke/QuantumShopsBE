package com.quantumluke.quantumshops.repository;

import com.quantumluke.quantumshops.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Category findByName(String name);

  boolean existsByName(String name);
}