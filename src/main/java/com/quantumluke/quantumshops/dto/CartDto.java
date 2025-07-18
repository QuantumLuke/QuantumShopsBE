package com.quantumluke.quantumshops.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link com.quantumluke.quantumshops.models.Cart}
 */
@Data
public class CartDto {
    private Long id;
    private BigDecimal totalPrice;
    private Set<CartItemDto> items;

}