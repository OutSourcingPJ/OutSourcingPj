package com.sparta.outsouringproject.cart.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class CartItemListInfo {

    private Long totalPrice;
    private List<CartItemInfo> items;

    public CartItemListInfo(List<CartItemInfo> cartItemList, Long totalPrice) {
        this.items = cartItemList;
        this.totalPrice = totalPrice;
    }
}
