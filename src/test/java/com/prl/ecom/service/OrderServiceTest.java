package com.prl.ecom.service;

import com.prl.ecom.dto.OrderDTO;
import com.prl.ecom.entity.*;
import com.prl.ecom.exception.BadRequestException;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPaymentRepository orderPaymentRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Order order;
    private Address address;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        address = Address.builder()
                .id(1L)
                .user(user)
                .street("123 Main St")
                .city("Springfield")
                .state("IL")
                .zipCode("62701")
                .country("USA")
                .build();

        order = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .user(user)
                .totalAmount(new BigDecimal("999.99"))
                .subtotal(new BigDecimal("900.00"))
                .shippingCost(new BigDecimal("99.99"))
                .tax(new BigDecimal("0"))
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .shippingAddress(address)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(new HashSet<>())
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new HashSet<>())
                .build();
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals("ORD-001", result.getOrderNumber());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void testGetUserOrders_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findByUserId(1L, pageable)).thenReturn(orderPage);

        var result = orderService.getUserOrders(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetUserOrders_ByOrderNumber_Success() {
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderByOrderNumber("ORD-001");

        assertNotNull(result);
        assertEquals("ORD-001", result.getOrderNumber());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED.name());

        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.cancelOrder(1L);

        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }
}
