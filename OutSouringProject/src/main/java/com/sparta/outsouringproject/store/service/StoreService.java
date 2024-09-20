package com.sparta.outsouringproject.store.service;

import com.sparta.outsouringproject.store.dto.*;

public interface StoreService {
    StoreResponseDto saveStore(CreateStoreRequestDto requestDto);
    GetStoreResponseDto getStore(Long storeId);
    GetStoreListResponseDto getStores();
    GetStoreListResponseDto getStoresByName(String name);
    StoreResponseDto modify(Long storeId, ModifyStoreRequestDto requestDto);
    void delete(Long storeId);
}
