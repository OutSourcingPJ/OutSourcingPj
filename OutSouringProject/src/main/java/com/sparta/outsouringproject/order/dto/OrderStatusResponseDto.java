package com.sparta.outsouringproject.order.dto;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatusResponseDto {
    private OrderStatus orderStatus;
}
