package com.sparta.outsouringproject.order.dto;

import com.sparta.outsouringproject.order.entity.OrderItem;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemInfo {

    private Long orderItemId;
    private Long menuId;
    private Long quantity;
    private Long price;
    private Long totalPrice;

    public OrderItemInfo(OrderItem orderItem){
        this.orderItemId = orderItem.getId();
        this.menuId = orderItem.getMenu().getMenuId();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        this.totalPrice = orderItem.getTotalPrice();
    }
}
