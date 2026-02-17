package com.prl.ecom.service;

import com.prl.ecom.dto.CartDTO;
import com.prl.ecom.dto.CartItemDTO;
import com.prl.ecom.entity.Cart;
import com.prl.ecom.entity.CartItem;
import com.prl.ecom.entity.Product;
import com.prl.ecom.entity.User;
import com.prl.ecom.exception.BadRequestException;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.CartItemRepository;
import com.prl.ecom.repository.CartRepository;
import com.prl.ecom.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .active(true)
                .build();
    }

    @Test
    void testGetCart_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartDTO result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetCart_NotFound() {
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCart(999L));
    }

    @Test
    void testAddToCart_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(new CartItem());

        CartDTO result = cartService.addToCart(1L, 1L, 2);

        assertNotNull(result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddToCart_ProductNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(1L, 999L, 2));
    }

    @Test
    void testAddToCart_InsufficientStock() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(BadRequestException.class, () -> cartService.addToCart(1L, 1L, 20));
    }

    @Test
    void testRemoveFromCart_Success() {
        CartItem cartItem = new CartItem();
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartService.removeFromCart(1L, 1L);

        verify(cartItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testClearCart_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);

        verify(cartItemRepository, times(1)).deleteByCartId(1L);
    }
}
