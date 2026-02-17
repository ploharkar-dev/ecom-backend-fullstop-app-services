package com.prl.ecom.controller;

import com.prl.ecom.dto.WishlistDTO;
import com.prl.ecom.service.WishlistService;
import com.prl.ecom.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Wishlist/Favorites endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get user wishlist")
    public ResponseEntity<ApiResponse<WishlistDTO>> getWishlist(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        WishlistDTO wishlist = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(ApiResponse.success(wishlist, "Wishlist retrieved successfully"));
    }

    @PostMapping("/items/{productId}")
    @Operation(summary = "Add product to wishlist")
    public ResponseEntity<ApiResponse<WishlistDTO>> addToWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        Long userId = getUserIdFromAuth(authentication);
        log.info("Adding to wishlist. User: {}, Product: {}", userId, productId);
        WishlistDTO wishlist = wishlistService.addItemToWishlist(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(wishlist, "Product added to wishlist", 201));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove product from wishlist")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        Long userId = getUserIdFromAuth(authentication);
        wishlistService.removeItemFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product removed from wishlist"));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // In a real app, fetch user ID from database using email from authentication.getPrincipal()
        return 1L;  // This should be replaced with actual user lookup
    }
}
