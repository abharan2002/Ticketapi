package com.kapture.ticket.exceptions;

public class ApiResponse<T> {
    private String status;
    private T data;
    private String message;

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and setters
}

