package com.quantumluke.quantumshops.controllers;

import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.Cart;
import com.quantumluke.quantumshops.models.User;
import com.quantumluke.quantumshops.response.ApiResponse;
import com.quantumluke.quantumshops.services.cart.ICartItemService;
import com.quantumluke.quantumshops.services.cart.ICartService;
import com.quantumluke.quantumshops.services.user.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cart-items")
public class CartItemController {

    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long itemId, @RequestParam int quantity) {
        try {
            User user = userService.getAuthentivatedUser();
            Cart cart = cartService.initializeCart(user);


            cartItemService.addItemToCart(cart.getId(), itemId, quantity);
            return ResponseEntity.ok(new ApiResponse("Item added successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error adding item to cart: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(@RequestParam Long cartId, @RequestParam Long itemId) {
        try {
            cartItemService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(new ApiResponse("Item removed successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error removing item from cart: " + e.getMessage(), null));
        }
    }

    @PutMapping("/update-quantity")
    public ResponseEntity<ApiResponse> updateItemQuantity(@RequestParam Long cartId, @RequestParam Long itemId, @RequestParam int quantity) {
        try {
            cartItemService.updateItemQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok(new ApiResponse("Item quantity updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error updating item quantity in cart: " + e.getMessage(), null));
        }
    }

}
