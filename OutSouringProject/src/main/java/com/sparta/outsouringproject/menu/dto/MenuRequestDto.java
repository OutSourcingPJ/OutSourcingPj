package com.sparta.outsouringproject.menu.dto;

import com.sparta.outsouringproject.store.entity.Store;
import lombok.Getter;

@Getter
public class MenuRequestDto {
    private String menuName;
    private String menuPrice;
    private Long storeId;

    public MenuRequestDto(String menuName, String menuPrice, Long storeId) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.storeId = storeId;
    }
}
