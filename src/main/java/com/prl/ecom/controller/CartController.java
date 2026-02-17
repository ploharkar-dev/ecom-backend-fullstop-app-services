package com.prl.ecom.controller;

import com.prl.ecom.dto.CartDTO;
import com.prl.ecom.dto.AddToCartRequest;
import com.prl.ecom.dto.UpdateCartItemRequest;
import com.prl.ecom.service.CartService;
import com.prl.ecom.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Shopping Cart", description = "Shopping cart endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @Operation(summary = "View cart")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart retrieved successfully"));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        Long userId = getUserIdFromAuth(authentication);
        log.info("Adding item to cart. User: {}, Product: {}", userId, request.getProductId());
        CartDTO cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cart, "Item added to cart", 201));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        Long userId = getUserIdFromAuth(authentication);
        CartDTO cart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart item updated successfully"));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            Authentication authentication,
            @PathVariable Long itemId) {
        Long userId = getUserIdFromAuth(authentication);
        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from cart"));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // In a real app, fetch user ID from database using email from authentication.getPrincipal()
        // For now, return a simple approach
        return 1L;  // This should be replaced with actual user lookup
    }
}
