package com.miananswer.stockflow.service;

import com.miananswer.stockflow.exception.DuplicateSkuException;
import com.miananswer.stockflow.exception.ProductNotFoundException;
import com.miananswer.stockflow.model.dto.CreateProductRequest;
import com.miananswer.stockflow.model.dto.ProductResponse;
import com.miananswer.stockflow.model.entity.Product;
import com.miananswer.stockflow.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private CreateProductRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateProductRequest(
                "LAPTOP-001",
                "Laptop",
                "Business laptop",
                new BigDecimal("999.99"),
                10
        );
    }

    @Test
    void createProduct_shouldCreateProduct() {
        when(productRepository.existsBySku("LAPTOP-001"))
                .thenReturn(false);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setSku("LAPTOP-001");
        savedProduct.setName("Laptop");
        savedProduct.setDescription("Business laptop");
        savedProduct.setPrice(new BigDecimal("999.99"));
        savedProduct.setQuantity(10);

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        assertEquals(1L, response.id());
        assertEquals("LAPTOP-001", response.sku());
        assertEquals("Laptop", response.name());
        assertEquals(new BigDecimal("999.99"), response.price());
        assertEquals(10, response.quantity());

        verify(productRepository).existsBySku("LAPTOP-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_shouldRejectDuplicateSku() {
        when(productRepository.existsBySku("LAPTOP-001"))
                .thenReturn(true);

        assertThrows(
                DuplicateSkuException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository).existsBySku("LAPTOP-001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProduct_shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> { productService.getProduct(999L); }
        );

        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
