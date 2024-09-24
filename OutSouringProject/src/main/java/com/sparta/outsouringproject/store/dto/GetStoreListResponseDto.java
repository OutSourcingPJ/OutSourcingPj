package com.sparta.outsouringproject.store.dto;

import com.sparta.outsouringproject.store.entity.Store;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
public class GetStoreListResponseDto {
    private List<StoreResponseDto> storeList;

    public GetStoreListResponseDto(List<StoreResponseDto> storeList) {
        this.storeList = storeList;
    }
}
