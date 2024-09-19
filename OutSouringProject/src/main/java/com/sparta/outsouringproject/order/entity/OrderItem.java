package com.sparta.outsouringproject.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Order order;

    private Long quantity;
    private Long price;
    private Long total_price;

    @Builder
    public OrderItem(Order order, /*Menu menu,*/ Long quantity, Long price, Long total_price) {
        this.order = order;
        this.quantity = quantity;
        this.price = price;
        this.total_price = total_price;
        // this.menu = menu;
    }

    // ManyToOne
    // private Menu menu;

    public void setOrder(Order order) {
        this.order = order;
        order.getItems().add(this);
    }
}
