package com.sparta.outsouringproject.order.dto;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import com.sparta.outsouringproject.order.entity.OrderItem;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemInfo {
    private Long orderId;
    private Long orderItemId;
    private OrderStatus orderStatus;
    private Long menuId;
    private Long quantity;
    private Long price;
    private Long totalPrice;

    public OrderItemInfo(OrderItem orderItem){
        this.orderId = orderItem.getOrder().getId();
        this.orderStatus = orderItem.getOrder().getStatus();
        this.orderItemId = orderItem.getId();
        this.menuId = orderItem.getMenu().getMenu_id();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        this.totalPrice = orderItem.getTotalPrice();
    }
}
