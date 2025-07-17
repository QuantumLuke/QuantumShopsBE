package com.quantumluke.quantumshops.services.order;

import com.quantumluke.quantumshops.models.Order;

import java.util.List;

public interface IOrderService {
    Order createOrder(Long userId);
    Order getOrderById(Long orderId);

    List<Order> getOrdersByUserId(Long userId);
}
