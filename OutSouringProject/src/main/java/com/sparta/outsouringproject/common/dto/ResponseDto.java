package com.sparta.outsouringproject.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto<T>{
    public int statusCode;
    public String message;
    public T data;


    public static <T> ResponseDto<T> of(int statusCode, String message, T data) {
        return new ResponseDto<T>(statusCode, message, data);
    }

    public static <T> ResponseDto<T> of(int statusCode, T data) {
        return new ResponseDto<T>(statusCode, "", data);
    }

    public static <T> ResponseDto<T> of(int statusCode, String message) {
        return new ResponseDto<T>(statusCode, "", null);
    }

    public static <T> ResponseDto<T> of(int statusCode) {
        return new ResponseDto<T>(statusCode, "", null);
    }
}


