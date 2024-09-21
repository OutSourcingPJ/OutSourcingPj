package com.sparta.outsouringproject.common.exceptions;

public class InvalidRequestException extends IllegalArgumentException{
    public InvalidRequestException(String message) {
        super(message);
    }
}
