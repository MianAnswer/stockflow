package com.miananswer.stockflow.controllers;

import com.miananswer.stockflow.models.dto.CreateOrderRequest;
import com.miananswer.stockflow.models.dto.OrderResponse;
import com.miananswer.stockflow.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
