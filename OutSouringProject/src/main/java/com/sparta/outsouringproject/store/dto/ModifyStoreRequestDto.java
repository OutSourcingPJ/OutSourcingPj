package com.sparta.outsouringproject.store.dto;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class ModifyStoreRequestDto {
    private String name;
    private Double orderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String advertise;
}
