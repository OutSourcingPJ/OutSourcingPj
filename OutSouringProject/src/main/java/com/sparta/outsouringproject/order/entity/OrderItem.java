package com.sparta.outsouringproject.order.entity;

import com.sparta.outsouringproject.cart.dto.CartItemInfo;
import com.sparta.outsouringproject.menu.entity.Menu;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name ="menu_id")
    private Menu menu;

    private Long quantity;
    private Long price;
    private Long totalPrice;

    @Builder
    public OrderItem(Order order, Menu menu, Long quantity, Long price, Long totalPrice) {
        this.order = order;
        this.menu = menu;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        order.getItems().add(this);
    }
}
