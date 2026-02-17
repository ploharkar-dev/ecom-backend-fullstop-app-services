package com.prl.ecom.service;

import com.prl.ecom.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    Page<ProductDTO> getAllProducts(Pageable pageable);
    ProductDTO getProductById(Long id);
    Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductDTO> searchProducts(String keyword, Pageable pageable);
    Page<ProductDTO> filterProductsByPrice(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
