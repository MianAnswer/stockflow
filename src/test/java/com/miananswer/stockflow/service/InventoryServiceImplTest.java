package com.miananswer.stockflow.service;

import com.miananswer.stockflow.exception.ProductNotFoundException;
import com.miananswer.stockflow.model.dto.AddInventoryRequest;
import com.miananswer.stockflow.model.dto.InventoryTransactionResponse;
import com.miananswer.stockflow.model.entity.InventoryTransaction;
import com.miananswer.stockflow.model.entity.InventoryTransactionType;
import com.miananswer.stockflow.model.entity.Product;
import com.miananswer.stockflow.repository.InventoryTransactionRepository;
import com.miananswer.stockflow.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product product;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setId(1L);
        product.setSku("LAPTOP-001");
        product.setName("Laptop");
        product.setDescription("Personal laptop");
        product.setPrice(BigDecimal.valueOf(499.99));
        product.setQuantity(10);
    }

    @Test
    public void addInventory_shouldIncreaseQuantityAndCreateTransaction() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        AddInventoryRequest request = new AddInventoryRequest(25);

        inventoryService.addInventory(1L, request);

        assertEquals(35, product.getQuantity());

        verify(productRepository).save(product);

        verify(inventoryTransactionRepository).save(any(InventoryTransaction.class));
    }

    @Test
    void getTransactions_shouldReturnProductTransactions() {

        InventoryTransaction transaction =
                new InventoryTransaction();

        transaction.setId(1L);
        transaction.setProduct(product);
        transaction.setType(
                InventoryTransactionType.STOCK_RECEIVED
        );
        transaction.setQuantity(25);
        transaction.setCreatedAt(Instant.now());

        when(productRepository.existsById(1L))
                .thenReturn(true);

        when(inventoryTransactionRepository
                .findByProductIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(transaction));

        List<InventoryTransactionResponse> result =
                inventoryService.getTransactions(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(1L, result.get(0).productId());
        assertEquals(
                InventoryTransactionType.STOCK_RECEIVED,
                result.get(0).type()
        );
        assertEquals(25, result.get(0).quantity());
    }

    @Test
    void getTransactions_shouldThrowWhenProductDoesNotExist() {

        when(productRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                ProductNotFoundException.class,
                () -> inventoryService.getTransactions(999L)
        );

        verify(
                inventoryTransactionRepository,
                never()
        ).findByProductIdOrderByCreatedAtDesc(anyLong());
    }
}
