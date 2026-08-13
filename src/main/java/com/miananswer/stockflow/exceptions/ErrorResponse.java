package com.miananswer.stockflow.exceptions;

public record ErrorResponse(int status, String error, String message) {
}
