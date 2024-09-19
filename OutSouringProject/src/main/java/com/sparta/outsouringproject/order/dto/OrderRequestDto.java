package com.sparta.outsouringproject.order.dto;

import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderRequestDto {
    private Long storeId;
    private List<CartItemInfo> carts;
}
