package com.quantumluke.quantumshops.services.cart;

import com.quantumluke.quantumshops.models.Cart;
import com.quantumluke.quantumshops.models.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCartById(Long id);
    Cart getCartByUserId(Long userId);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initializeCart(User user);

}
