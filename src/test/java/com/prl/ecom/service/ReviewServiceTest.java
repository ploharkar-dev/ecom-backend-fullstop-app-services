package com.prl.ecom.service;

import com.prl.ecom.dto.ReviewDTO;
import com.prl.ecom.entity.Product;
import com.prl.ecom.entity.Review;
import com.prl.ecom.entity.User;
import com.prl.ecom.exception.DuplicateException;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.ProductRepository;
import com.prl.ecom.repository.ReviewRepository;
import com.prl.ecom.repository.UserRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .active(true)
                .averageRating(new BigDecimal("4.5"))
                .totalReviews(5)
                .reviews(new HashSet<>())
                .build();

        review = Review.builder()
                .id(1L)
                .product(product)
                .user(user)
                .rating(new BigDecimal("4.5"))
                .comment("Great product")
                .helpfulCount(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetProductReviews_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
        when(reviewRepository.findByProductId(1L, pageable)).thenReturn(reviewPage);

        var result = reviewService.getProductReviews(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetReviewById_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ReviewDTO result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals("Great product", result.getComment());
    }

    @Test
    void testGetReviewById_NotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.getReviewById(999L));
    }

    @Test
    void testAddReview_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByProductIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDTO result = reviewService.addReview(1L, 1L, new BigDecimal("4.5"), "Great product");

        assertNotNull(result);
        assertEquals("Great product", result.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testAddReview_DuplicateReview() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByProductIdAndUserId(1L, 1L)).thenReturn(Optional.of(review));

        assertThrows(DuplicateException.class, () -> 
            reviewService.addReview(1L, 1L, new BigDecimal("4.5"), "Another review"));
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }
}
