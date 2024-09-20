package com.sparta.outsouringproject.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto<T>{
    public int statusCode;
    public String message = "";
    public T data;

    @Builder
    public ResponseDto(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    /**
     * 데이터가 없는 경우
     * @param statusCode
     * @param message
     */
    public ResponseDto(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    /**
     * 상태코드만 반환하는 경우
     * @param statusCode
     */
    public ResponseDto(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * 메세지만 없는 경우
     * @param statusCode
     * @param data
     */
    public ResponseDto(int statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }
}
