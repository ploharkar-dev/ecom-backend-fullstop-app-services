package com.prl.ecom.controller;

import com.prl.ecom.dto.OrderDTO;
import com.prl.ecom.dto.CreateOrderRequest;
import com.prl.ecom.service.OrderService;
import com.prl.ecom.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints (OMS)")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order (Checkout)")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {
        Long userId = getUserIdFromAuth(authentication);
        log.info("Creating order for user: {}", userId);
        OrderDTO order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order created successfully", 201));
    }

    @GetMapping
    @Operation(summary = "Get user orders")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getUserOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getUserIdFromAuth(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(
            Authentication authentication,
            @PathVariable Long orderId) {
        Long userId = getUserIdFromAuth(authentication);
        OrderDTO order = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(order, "Order retrieved successfully"));
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderDTO order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // In a real app, fetch user ID from database using email from authentication.getPrincipal()
        return 1L;  // This should be replaced with actual user lookup
    }
}
