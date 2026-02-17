package com.prl.ecom.controller;

import com.prl.ecom.dto.ReviewDTO;
import com.prl.ecom.dto.AddReviewRequest;
import com.prl.ecom.service.ReviewService;
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
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Product reviews and ratings endpoints")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{productId}")
    @Operation(summary = "Add review to product")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<ReviewDTO>> addReview(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody AddReviewRequest request) {
        Long userId = getUserIdFromAuth(authentication);
        log.info("Adding review. Product: {}, User: {}", productId, userId);
        ReviewDTO review = reviewService.addReview(productId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(review, "Review added successfully", 201));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product reviews")
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Reviews retrieved successfully"));
    }

    @GetMapping("/user")
    @Operation(summary = "Get user reviews")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getUserReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserIdFromAuth(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Reviews retrieved successfully"));
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update review")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody AddReviewRequest request) {
        Long userId = getUserIdFromAuth(authentication);
        ReviewDTO review = reviewService.updateReview(reviewId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(review, "Review updated successfully"));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete review")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId) {
        Long userId = getUserIdFromAuth(authentication);
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Review deleted successfully"));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        String email = authentication.getPrincipal().toString();
        return 1L;  // This should be replaced with actual user lookup
    }
}
