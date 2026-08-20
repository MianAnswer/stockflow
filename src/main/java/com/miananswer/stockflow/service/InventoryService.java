package com.miananswer.stockflow.service;

import com.miananswer.stockflow.model.dto.AddInventoryRequest;
import com.miananswer.stockflow.model.dto.InventoryTransactionResponse;

import java.util.List;

public interface InventoryService {

    void addInventory(Long productId, AddInventoryRequest request);

    List<InventoryTransactionResponse> getTransactions(Long productId);
}
