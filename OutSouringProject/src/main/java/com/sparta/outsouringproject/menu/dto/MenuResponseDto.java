package com.sparta.outsouringproject.menu.dto;

import lombok.Getter;

@Getter
public class MenuResponseDto {
    private final String menuName;
    private final String menuPrice;

    public MenuResponseDto(String menuName, String menuPrice) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }
}
