package com.miananswer.stockflow.service;

import com.miananswer.stockflow.exception.InsufficientInventoryException;
import com.miananswer.stockflow.exception.ProductNotFoundException;
import com.miananswer.stockflow.model.dto.CreateOrderItemRequest;
import com.miananswer.stockflow.model.dto.CreateOrderRequest;
import com.miananswer.stockflow.model.dto.OrderResponse;
import com.miananswer.stockflow.model.entity.InventoryTransaction;
import com.miananswer.stockflow.model.entity.InventoryTransactionType;
import com.miananswer.stockflow.model.entity.Order;
import com.miananswer.stockflow.model.entity.Product;
import com.miananswer.stockflow.repository.InventoryTransactionRepository;
import com.miananswer.stockflow.repository.OrderRepository;
import com.miananswer.stockflow.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setSku("LAPTOP-001");
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setQuantity(10);
    }

    @Test
    void createOrder_shouldCreateOrderAndDecreaseInventory() {
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(1L, 2);

        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        OrderResponse response = orderService.createOrder(request);

        assertEquals(1L, response.id());
        assertEquals("CREATED", response.status().name());
        assertEquals(
                new BigDecimal("1999.98"),
                response.total()
        );

        assertEquals(1, response.items().size());

        assertEquals(2, response.items().get(0).quantity());
        assertEquals(
                new BigDecimal("999.99"),
                response.items().get(0).unitPrice()
        );

        assertEquals(8, product.getQuantity());

        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(inventoryTransactionRepository).save(any(InventoryTransaction.class));

        ArgumentCaptor<InventoryTransaction> captor = ArgumentCaptor.forClass(InventoryTransaction.class);

        verify(inventoryTransactionRepository).save(captor.capture());

        InventoryTransaction transaction = captor.getValue();

        assertEquals(InventoryTransactionType.ORDER_PLACED, transaction.getType());
        assertEquals(-2, transaction.getQuantity());
        assertEquals(product, transaction.getProduct());
    }

    @Test
    void createOrder_shouldThrowWhenProductDoesNotExist() {
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(999L, 2);

        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> { orderService.createOrder(request); }
        );

        verify(productRepository).findById(999L);

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowWhenInventoryIsInsufficient() {
        product.setQuantity(1);

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest(1L, 2);

        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientInventoryException.class,
                () -> orderService.createOrder(request)
        );

        assertEquals(1, product.getQuantity());

        verify(productRepository).findById(1L);

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void createOrder_shouldHandleMultipleProducts() {

        Product mouse = new Product();
        mouse.setId(2L);
        mouse.setSku("MOUSE-001");
        mouse.setName("Mouse");
        mouse.setPrice(new BigDecimal("25.00"));
        mouse.setQuantity(20);

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(1L, 2),
                        new CreateOrderItemRequest(2L, 3)
                )
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(mouse));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        OrderResponse response = orderService.createOrder(request);

        BigDecimal expectedTotal = new BigDecimal("1999.98")
                        .add(new BigDecimal("75.00"));

        assertEquals(expectedTotal, response.total());

        assertEquals(2, response.items().size());

        assertEquals(8, product.getQuantity());
        assertEquals(17, mouse.getQuantity());

        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(orderRepository).save(any(Order.class));
    }
}
