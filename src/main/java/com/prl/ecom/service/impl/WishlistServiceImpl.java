package com.prl.ecom.service.impl;

import com.prl.ecom.dto.WishlistDTO;
import com.prl.ecom.dto.WishlistItemDTO;
import com.prl.ecom.entity.Product;
import com.prl.ecom.entity.User;
import com.prl.ecom.entity.Wishlist;
import com.prl.ecom.entity.WishlistItem;
import com.prl.ecom.exception.BadRequestException;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.ProductRepository;
import com.prl.ecom.repository.WishlistRepository;
import com.prl.ecom.repository.WishlistItemRepository;
import com.prl.ecom.repository.UserRepository;
import com.prl.ecom.service.WishlistService;
import com.prl.ecom.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public WishlistDTO getWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        return convertToDTO(wishlist);
    }

    @Override
    public WishlistDTO addItemToWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND));

        // Check if product already in wishlist
        if (wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId).isPresent()) {
            throw new BadRequestException("Product already in wishlist");
        }

        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();

        wishlistItemRepository.save(item);
        log.info("Item added to wishlist. User: {}, Product: {}", userId, productId);

        return getWishlist(userId);
    }

    @Override
    public void removeItemFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        WishlistItem item = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in wishlist"));

        wishlistItemRepository.delete(item);
        log.info("Item removed from wishlist. User: {}, Product: {}", userId, productId);
    }

    @Override
    public void initializeWishlist(User user) {
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .build();
        wishlistRepository.save(wishlist);
    }

    private WishlistDTO convertToDTO(Wishlist wishlist) {
        var items = wishlist.getItems().stream()
                .map(item -> {
                    String imageUrl = item.getProduct().getImages().stream()
                            .findFirst()
                            .map(img -> img.getImageUrl())
                            .orElse(null);
                    return new WishlistItemDTO(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getPrice(),
                            imageUrl
                    );
                })
                .collect(Collectors.toList());

        return new WishlistDTO(wishlist.getId(), items);
    }
}
