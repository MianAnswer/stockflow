package com.miananswer.stockflow.models.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty
        @Valid
        List<CreateOrderItemRequest> items
) {
}
