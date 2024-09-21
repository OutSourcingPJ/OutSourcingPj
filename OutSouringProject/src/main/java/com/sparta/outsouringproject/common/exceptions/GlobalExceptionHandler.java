package com.sparta.outsouringproject.common.exceptions;

import com.sparta.outsouringproject.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( { IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> IllegalArgExceptionHanlder(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler( {AuthException.class} )
    public ResponseEntity<ErrorResponse> authExceptionHanlder(AuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler( { AccessDeniedException.class} )
    public ResponseEntity<ErrorResponse> authExceptionHanlder(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }
}
