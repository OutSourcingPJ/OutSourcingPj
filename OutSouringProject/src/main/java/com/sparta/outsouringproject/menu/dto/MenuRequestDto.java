package com.sparta.outsouringproject.menu.dto;

import com.sparta.outsouringproject.store.entity.Store;
import lombok.Getter;

@Getter
public class MenuRequestDto {
    private String menuName;
    private String menuPrice;
    private Long storeId;
}
