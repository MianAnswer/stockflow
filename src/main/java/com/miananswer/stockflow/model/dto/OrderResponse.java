package com.miananswer.stockflow.model.dto;

import com.miananswer.stockflow.model.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal total,
        List<OrderItemResponse> items
) {
}
