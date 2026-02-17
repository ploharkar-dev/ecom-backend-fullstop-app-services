package com.prl.ecom.service;

import com.prl.ecom.dto.CartDTO;
import com.prl.ecom.dto.AddToCartRequest;
import com.prl.ecom.dto.UpdateCartItemRequest;
import com.prl.ecom.entity.User;

public interface CartService {
    CartDTO getCart(Long userId);
    CartDTO addItemToCart(Long userId, AddToCartRequest request);
    CartDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);
    void removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
    void initializeCart(User user);
}
