package com.quantumluke.quantumshops.services.order;

import com.quantumluke.quantumshops.models.Order;

public interface IOrderService {
    Order createOrder(Long userId);
    Order getOrderById(Long orderId);
}
