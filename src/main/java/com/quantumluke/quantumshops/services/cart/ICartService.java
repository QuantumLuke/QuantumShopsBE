package com.quantumluke.quantumshops.services.cart;

import com.quantumluke.quantumshops.models.Cart;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCartById(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);
}
