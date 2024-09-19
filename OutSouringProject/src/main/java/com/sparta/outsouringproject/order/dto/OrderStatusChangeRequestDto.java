package com.sparta.outsouringproject.order.dto;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusChangeRequestDto {
    private OrderStatus orderStatus;
}
