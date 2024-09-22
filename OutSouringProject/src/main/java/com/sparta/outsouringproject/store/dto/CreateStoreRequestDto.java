package com.sparta.outsouringproject.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class CreateStoreRequestDto {
    private String name;
    private Double orderAmount;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime closeTime;
}
