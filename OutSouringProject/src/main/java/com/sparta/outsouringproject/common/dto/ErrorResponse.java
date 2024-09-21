package com.sparta.outsouringproject.common.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    int statusCode;
    String message;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ErrorResponse(HttpStatus statusCode, String message) {
        this.statusCode = statusCode.value();
        this.message = message;
    }

    public ErrorResponse(HttpStatus statusCode) {
        this.statusCode = statusCode.value();
        this.message = "";
    }

    public ErrorResponse(int statusCode) {
        this.statusCode = statusCode;
        this.message = "";
    }
}
