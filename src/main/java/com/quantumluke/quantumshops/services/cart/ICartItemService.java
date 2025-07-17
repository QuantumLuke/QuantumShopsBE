package com.quantumluke.quantumshops.services.cart;

import com.quantumluke.quantumshops.models.Cart;
import com.quantumluke.quantumshops.models.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);
    void updateItemQuantity(Long cartId, Long productId, int quantity);

    CartItem getCartItem(Long productId, Cart cart);
}
