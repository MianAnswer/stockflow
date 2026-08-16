package com.miananswer.stockflow.service;

import com.miananswer.stockflow.model.dto.CreateOrderRequest;
import com.miananswer.stockflow.model.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
}
