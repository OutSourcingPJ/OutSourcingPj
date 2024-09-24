package com.sparta.outsouringproject.order.dto;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCreateResponseDto {

    private Long userId;
    private Long storeId;
    private Long orderId;
    private List<OrderItemInfo> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
