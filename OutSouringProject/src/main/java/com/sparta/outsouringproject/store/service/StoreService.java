package com.sparta.outsouringproject.store.service;

import com.sparta.outsouringproject.store.dto.*;

public interface StoreService {
    StoreResponseDto saveStore(CreateStoreRequestDto requestDto, String email);
    GetStoreResponseDto getStore(Long storeId);
    GetStoreListResponseDto getStores();
    GetStoreListResponseDto getStoresByName(String name);
    StoreResponseDto modify(Long storeId, ModifyStoreRequestDto requestDto);
    void delete(Long storeId);
    void checkAdvertise(Long storeId);
    void unCheckAdvertise(Long storeId);
}
