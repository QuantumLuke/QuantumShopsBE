package com.quantumluke.quantumshops.services.order;

import com.quantumluke.quantumshops.dto.OrderDto;
import com.quantumluke.quantumshops.models.Order;

import java.util.List;

public interface IOrderService {
    Order createOrder(Long userId);
    OrderDto getOrderById(Long orderId);

    List<OrderDto> getOrdersByUserId(Long userId);

    OrderDto convertToDto(Order order);
}
