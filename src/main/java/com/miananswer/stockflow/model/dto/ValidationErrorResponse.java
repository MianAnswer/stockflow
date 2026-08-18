package com.miananswer.stockflow.model.dto;

import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> errors
) {
}
