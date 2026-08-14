package com.miananswer.stockflow.integration;

import com.miananswer.stockflow.exceptions.InsufficientInventoryException;
import com.miananswer.stockflow.models.dto.CreateOrderItemRequest;
import com.miananswer.stockflow.models.dto.CreateOrderRequest;
import com.miananswer.stockflow.models.entity.Product;
import com.miananswer.stockflow.repositories.OrderRepository;
import com.miananswer.stockflow.repositories.ProductRepository;
import com.miananswer.stockflow.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class OrderIntegrationTest {

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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Test
    void createOrder_shouldRollbackWhenInventoryIsInsufficient() {

        Product laptop = new Product();
        laptop.setSku("LAPTOP-001");
        laptop.setName("Laptop");
        laptop.setDescription("Laptop");
        laptop.setPrice(new BigDecimal("1000.00"));
        laptop.setQuantity(5);

        Product mouse = new Product();
        mouse.setSku("MOUSE-001");
        mouse.setName("Mouse");
        mouse.setDescription("Mouse");
        mouse.setPrice(new BigDecimal("25.00"));
        mouse.setQuantity(0);

        productRepository.save(laptop);
        productRepository.save(mouse);

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(laptop.getId(), 2),
                        new CreateOrderItemRequest(mouse.getId(), 1)
                )
        );

        assertThrows(
                InsufficientInventoryException.class,
                () -> orderService.createOrder(request)
        );

        Product savedLaptop = productRepository
                .findById(laptop.getId())
                .orElseThrow();

        assertEquals(5, savedLaptop.getQuantity());

        assertEquals(0, orderRepository.count());
    }
}
