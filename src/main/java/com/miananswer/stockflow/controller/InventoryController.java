package com.miananswer.stockflow.controller;

import com.miananswer.stockflow.model.dto.AddInventoryRequest;
import com.miananswer.stockflow.model.dto.InventoryTransactionResponse;
import com.miananswer.stockflow.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<Void> postInventory(
            @PathVariable Long productId,
            @Valid @RequestBody AddInventoryRequest request) {
        inventoryService.addInventory(productId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransactionResponse>> getInventory(@PathVariable Long productId) {
        List<InventoryTransactionResponse> transactions = inventoryService.getTransactions(productId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactions);
    }
}
