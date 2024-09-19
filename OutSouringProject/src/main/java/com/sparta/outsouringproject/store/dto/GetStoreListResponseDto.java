package com.sparta.outsouringproject.store.dto;

import com.sparta.outsouringproject.store.entity.Store;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class GetStoreListResponseDto {
    private String name;
    private Double orderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;

    public GetStoreListResponseDto(Store store) {
        this.name = store.getName();
        this.orderAmount = store.getOrderAmount();
        this.openTime = store.getOpenTime();
        this.closeTime = store.getCloseTime();
    }
}
