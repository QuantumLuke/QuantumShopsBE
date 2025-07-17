package com.quantumluke.quantumshops.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for {@link com.quantumluke.quantumshops.models.CartItem}
 */
@Data
public class CartItemDto {
    private Long id;
    private int quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}