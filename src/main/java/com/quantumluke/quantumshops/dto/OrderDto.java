package com.quantumluke.quantumshops.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private String status;
    private List<OrderItemDto> orderItems;
}
