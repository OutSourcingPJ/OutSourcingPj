package com.sparta.outsouringproject.cart.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItemInfo {

    private Long cartItemId;
    private Long menuId;
    private Long quantity;
    private Long price;
    private Long totalPrice;
}
