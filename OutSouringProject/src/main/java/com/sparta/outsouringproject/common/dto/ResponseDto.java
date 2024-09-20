package com.sparta.outsouringproject.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto<T>{
    public int statusCode;
    public String message;
    public T data;


    public static <T> ResponseDto<T> of(int statusCode, String message, T data) {
        return ResponseDto.<T>builder()
            .statusCode(statusCode)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ResponseDto<T> of(int statusCode, T data) {
        return ResponseDto.<T>builder()
            .statusCode(statusCode)
            .message("")
            .data(data)
            .build();
    }

    public static <T> ResponseDto<T> of(int statusCode, String message) {
        return ResponseDto.<T>builder()
            .statusCode(statusCode)
            .message(message)
            .build();
    }

    public static <T> ResponseDto<T> of(int statusCode) {
        return ResponseDto.<T>builder()
            .statusCode(statusCode)
            .build();
    }
}


