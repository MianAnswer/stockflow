package com.miananswer.stockflow.services;

import com.miananswer.stockflow.models.dto.CreateOrderRequest;
import com.miananswer.stockflow.models.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
}
