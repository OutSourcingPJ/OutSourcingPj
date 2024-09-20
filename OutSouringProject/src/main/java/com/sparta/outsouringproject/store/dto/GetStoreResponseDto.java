package com.sparta.outsouringproject.store.dto;

import com.sparta.outsouringproject.menu.dto.MenuResponseDto;
import com.sparta.outsouringproject.menu.entity.Menu;
import com.sparta.outsouringproject.store.entity.Store;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetStoreResponseDto {
    private String name;
    private Double orderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;
    private List<MenuResponseDto> menuList;

    public GetStoreResponseDto(Store store) {
        this.name = store.getName();
        this.orderAmount = store.getOrderAmount();
        this.openTime = store.getOpenTime();
        this.closeTime = store.getCloseTime();
        this.menuList = store.getMenuList().stream()
                .map(menu -> new MenuResponseDto(menu.getMenuName(), menu.getMenuPrice()))
                .collect(Collectors.toList());
    }
}
