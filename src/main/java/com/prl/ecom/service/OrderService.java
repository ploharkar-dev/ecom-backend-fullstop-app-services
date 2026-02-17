package com.prl.ecom.service;

import com.prl.ecom.dto.OrderDTO;
import com.prl.ecom.dto.CreateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(Long userId, CreateOrderRequest request);
    OrderDTO getOrderById(Long userId, Long orderId);
    Page<OrderDTO> getUserOrders(Long userId, Pageable pageable);
    OrderDTO updateOrderStatus(Long orderId, String status);
}
