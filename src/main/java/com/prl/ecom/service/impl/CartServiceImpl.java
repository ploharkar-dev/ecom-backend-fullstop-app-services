package com.prl.ecom.service.impl;

import com.prl.ecom.dto.CartDTO;
import com.prl.ecom.dto.CartItemDTO;
import com.prl.ecom.dto.AddToCartRequest;
import com.prl.ecom.dto.UpdateCartItemRequest;
import com.prl.ecom.entity.Cart;
import com.prl.ecom.entity.CartItem;
import com.prl.ecom.entity.Product;
import com.prl.ecom.entity.User;
import com.prl.ecom.exception.BadRequestException;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.CartRepository;
import com.prl.ecom.repository.CartItemRepository;
import com.prl.ecom.repository.ProductRepository;
import com.prl.ecom.service.CartService;
import com.prl.ecom.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));
        return convertToDTO(cart);
    }

    @Override
    public CartDTO addItemToCart(Long userId, AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new BadRequestException(AppConstants.INVALID_QUANTITY);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND));

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException(AppConstants.INSUFFICIENT_STOCK);
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
        }

        cartItemRepository.save(cartItem);
        log.info("Product added to cart. User: {}, Product: {}, Quantity: {}", userId, product.getId(), request.getQuantity());

        return getCart(userId);
    }

    @Override
    public CartDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        if (request.getQuantity() <= 0) {
            throw new BadRequestException(AppConstants.INVALID_QUANTITY);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
        }

        if (cartItem.getProduct().getStock() < request.getQuantity()) {
            throw new BadRequestException(AppConstants.INSUFFICIENT_STOCK);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
        }

        cartItemRepository.delete(cartItem);
        log.info("Cart item removed. User: {}, CartItem: {}", userId, cartItemId);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));

        cartItemRepository.deleteByCartId(cart.getId());
        log.info("Cart cleared for user: {}", userId);
    }

    @Override
    public void initializeCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();
        cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        var items = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice(),
                        item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        dto.setItems(items);
        dto.setTotalItems(items.stream().mapToInt(CartItemDTO::getQuantity).sum());
        dto.setTotalPrice(items.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return dto;
    }
}
