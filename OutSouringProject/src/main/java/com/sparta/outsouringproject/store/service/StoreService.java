package com.sparta.outsouringproject.store.service;

import com.sparta.outsouringproject.common.dto.AuthUser;
import com.sparta.outsouringproject.store.dto.*;

public interface StoreService {
    StoreResponseDto saveStore(CreateStoreRequestDto requestDto, AuthUser authUser);
    GetStoreResponseDto getStore(Long storeId);
    GetStoreListResponseDto getStores();
    GetStoreListResponseDto getStoresByName(String name);
    StoreResponseDto modify(Long storeId, ModifyStoreRequestDto requestDto, AuthUser authUser);
    void delete(Long storeId, AuthUser authUser);
    void checkAdvertise(Long storeId, AuthUser authUser);
    void unCheckAdvertise(Long storeId, AuthUser authUser);
}
