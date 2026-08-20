package com.miananswer.stockflow.service;

import com.miananswer.stockflow.exception.ProductNotFoundException;
import com.miananswer.stockflow.model.dto.AddInventoryRequest;
import com.miananswer.stockflow.model.dto.InventoryTransactionResponse;
import com.miananswer.stockflow.model.entity.InventoryTransaction;
import com.miananswer.stockflow.model.entity.InventoryTransactionType;
import com.miananswer.stockflow.model.entity.Product;
import com.miananswer.stockflow.repository.InventoryTransactionRepository;
import com.miananswer.stockflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public InventoryServiceImpl(
            ProductRepository productRepository,
            InventoryTransactionRepository inventoryTransactionRepository
    ) {

        this.productRepository = productRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }

    @Override
    @Transactional
    public void addInventory(Long productId, AddInventoryRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setQuantity(product.getQuantity() + request.quantity());

        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setProduct(product);
        inventoryTransaction.setType(InventoryTransactionType.STOCK_RECEIVED);
        inventoryTransaction.setQuantity(request.quantity());
        inventoryTransaction.setCreatedAt(Instant.now());

        productRepository.save(product);
        inventoryTransactionRepository.save(inventoryTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactions(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }

        return inventoryTransactionRepository
                .findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private InventoryTransactionResponse toResponse(
            InventoryTransaction transaction) {

        return new InventoryTransactionResponse(
                transaction.getId(),
                transaction.getProduct().getId(),
                transaction.getType(),
                transaction.getQuantity(),
                transaction.getCreatedAt()
        );
    }

}
