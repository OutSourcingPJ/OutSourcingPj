package com.sparta.outsouringproject.cart.dto;

import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder
    public CartItemInfo(Long cartItemId, Long menuId, Long quantity, Long price, Long totalPrice) {
        this.cartItemId = cartItemId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price * quantity;
    }
}
