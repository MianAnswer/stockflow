package com.miananswer.stockflow.models.dto;

import com.miananswer.stockflow.models.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal total,
        List<OrderItemResponse> items
) {
}
