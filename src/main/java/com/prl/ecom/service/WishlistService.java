package com.prl.ecom.service;

import com.prl.ecom.dto.WishlistDTO;
import com.prl.ecom.entity.User;

public interface WishlistService {
    WishlistDTO getWishlist(Long userId);
    WishlistDTO addItemToWishlist(Long userId, Long productId);
    void removeItemFromWishlist(Long userId, Long productId);
    void initializeWishlist(User user);
}
