package com.quantumluke.quantumshops.repository;

import com.quantumluke.quantumshops.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}