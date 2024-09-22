package com.sparta.outsouringproject.store.dto;

import com.sparta.outsouringproject.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class StoreResponseDto {
    private String name;
    private Double orderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String notice;

    public StoreResponseDto(Store store) {
        this.name = store.getName();
        this.orderAmount = store.getOrderAmount();
        this.openTime = store.getOpenTime();
        this.closeTime = store.getCloseTime();
        this.notice = store.getNotice();
    }

}
