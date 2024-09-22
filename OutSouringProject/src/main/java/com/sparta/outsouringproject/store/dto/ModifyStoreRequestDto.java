package com.sparta.outsouringproject.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyStoreRequestDto {
    private String name;
    private Double orderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String notice;
}
