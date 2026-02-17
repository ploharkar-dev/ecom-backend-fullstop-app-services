package com.prl.ecom.service;

import com.prl.ecom.dto.ReviewDTO;
import com.prl.ecom.dto.AddReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewDTO addReview(Long productId, Long userId, AddReviewRequest request);
    ReviewDTO updateReview(Long reviewId, Long userId, AddReviewRequest request);
    void deleteReview(Long reviewId, Long userId);
    Page<ReviewDTO> getProductReviews(Long productId, Pageable pageable);
    Page<ReviewDTO> getUserReviews(Long userId, Pageable pageable);
}
