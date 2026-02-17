package com.prl.ecom.service.impl;

import com.prl.ecom.dto.ReviewDTO;
import com.prl.ecom.dto.AddReviewRequest;
import com.prl.ecom.entity.Product;
import com.prl.ecom.entity.Review;
import com.prl.ecom.entity.User;
import com.prl.ecom.exception.BadRequestException;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.ProductRepository;
import com.prl.ecom.repository.ReviewRepository;
import com.prl.ecom.repository.UserRepository;
import com.prl.ecom.service.ReviewService;
import com.prl.ecom.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ReviewDTO addReview(Long productId, Long userId, AddReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

        // Check if user already reviewed this product
        if (reviewRepository.findByProductIdAndUserId(productId, userId).isPresent()) {
            throw new BadRequestException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // Update product average rating and review count
        updateProductRating(product);

        log.info("Review added. Product: {}, User: {}, Rating: {}", productId, userId, request.getRating());

        return convertToDTO(savedReview);
    }

    @Override
    public ReviewDTO updateReview(Long reviewId, Long userId, AddReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);

        // Update product average rating
        updateProductRating(review.getProduct());

        log.info("Review updated. Review ID: {}", reviewId);

        return convertToDTO(updatedReview);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);

        // Update product average rating
        updateProductRating(product);

        log.info("Review deleted. Review ID: {}", reviewId);
    }

    @Override
    public Page<ReviewDTO> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<ReviewDTO> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    private void updateProductRating(Product product) {
        var reviews = product.getReviews();
        if (reviews.isEmpty()) {
            product.setAverageRating(BigDecimal.ZERO);
            product.setTotalReviews(0);
        } else {
            BigDecimal totalRating = reviews.stream()
                    .map(Review::getRating)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal averageRating = totalRating.divide(BigDecimal.valueOf(reviews.size()), 2, java.math.RoundingMode.HALF_UP);
            product.setAverageRating(averageRating);
            product.setTotalReviews(reviews.size());
        }
        productRepository.save(product);
    }

    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getProduct().getId(),
                review.getUser().getId(),
                review.getUser().getFirstName() + " " + review.getUser().getLastName(),
                review.getRating(),
                review.getComment(),
                review.getHelpfulCount(),
                review.getCreatedAt()
        );
    }
}
