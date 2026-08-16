package com.miananswer.stockflow.exception;

public record ErrorResponse(int status, String error, String message) {
}
