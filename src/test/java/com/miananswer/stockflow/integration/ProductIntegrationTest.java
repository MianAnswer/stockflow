package com.miananswer.stockflow.integration;

import com.miananswer.stockflow.models.entity.Product;
import com.miananswer.stockflow.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
public class ProductIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndRetrieveProduct() {
        Product product = new Product();

        product.setSku("TEST-001");
        product.setName("Test Product");
        product.setDescription("Integration test product");
        product.setPrice(new BigDecimal("25.00"));
        product.setQuantity(10);

        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());

        Product retrieved = productRepository
                .findById(saved.getId())
                .orElseThrow();

        assertEquals("TEST-001", retrieved.getSku());
        assertEquals("Test Product", retrieved.getName());
        assertEquals(new BigDecimal("25.00"), retrieved.getPrice());
        assertEquals(10, retrieved.getQuantity());
    }

}
