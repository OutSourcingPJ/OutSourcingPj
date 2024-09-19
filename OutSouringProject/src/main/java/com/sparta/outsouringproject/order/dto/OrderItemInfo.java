package com.sparta.outsouringproject.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemInfo {

    private final Long orderItemId;
    private final Long menuId;
    private final Long quantity;
    private final Long price;
    private final Long totalPrice;
}
