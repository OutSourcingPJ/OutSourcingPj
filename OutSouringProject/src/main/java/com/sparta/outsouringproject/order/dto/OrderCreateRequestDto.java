package com.sparta.outsouringproject.order.dto;

import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreateRequestDto {
    private Long storeId;
    private List<CartItemInfo> carts;
}
