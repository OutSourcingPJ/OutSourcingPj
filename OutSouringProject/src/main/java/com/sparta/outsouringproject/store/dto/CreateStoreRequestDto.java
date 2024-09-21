package com.sparta.outsouringproject.store.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
public class CreateStoreRequestDto {
    private String name;
    private Double orderAmount;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    /**
     * 생성자: 정재호
     * 생성 이유: 테스트 데이터 생성할 때 엔티티 만들 때 필요해서 생성해주기 위함
     */
    @Builder
    public CreateStoreRequestDto(String name, Double orderAmount, LocalTime openTime, LocalTime closeTime) {
        this.name = name;
        this.orderAmount = orderAmount;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
