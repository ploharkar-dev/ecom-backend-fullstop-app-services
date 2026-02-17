package com.prl.ecom.service;

import com.prl.ecom.dto.ProductDTO;
import com.prl.ecom.entity.Category;
import com.prl.ecom.entity.Product;
import com.prl.ecom.entity.ProductImage;
import com.prl.ecom.exception.ResourceNotFoundException;
import com.prl.ecom.repository.ProductRepository;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronics category")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High-end laptop")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .sku("LAPTOP001")
                .category(category)
                .averageRating(new BigDecimal("4.5"))
                .totalReviews(5)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .images(new HashSet<>())
                .build();

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop");
        productDTO.setPrice(new BigDecimal("999.99"));
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void testGetAllProducts_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findByActiveTrue(pageable)).thenReturn(productPage);

        var result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetProductsByCategory_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findByCategoryIdAndActiveTrue(1L, pageable)).thenReturn(productPage);

        var result = productService.getProductsByCategory(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearchProducts_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findByNameContainingIgnoreCaseAndActiveTrue("Laptop", pageable))
                .thenReturn(productPage);

        var result = productService.searchProducts("Laptop", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testCheckProductStock_Sufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean result = productService.checkProductStock(1L, 5);

        assertTrue(result);
    }

    @Test
    void testCheckProductStock_Insufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean result = productService.checkProductStock(1L, 20);

        assertFalse(result);
    }
}
