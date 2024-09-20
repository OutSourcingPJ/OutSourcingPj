package com.sparta.outsouringproject.menu.dto;

import lombok.Getter;

@Getter
public class MenuResponseDto {
    private final String menuName;
    private final Long menuPrice;

    public MenuResponseDto(String menuName, Long menuPrice) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }
}
