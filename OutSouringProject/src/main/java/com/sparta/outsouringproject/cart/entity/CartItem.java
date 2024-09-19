package com.sparta.outsouringproject.cart.entity;

import com.sparta.outsouringproject.menu.entity.Menu;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;
    private Long price;
    private Long totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="cart_id")
    private Cart cart;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="menu_id")
    private Menu menu;

    public CartItem(Long quantity, Long price, Cart cart, Menu menu){
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price * quantity;
        this.cart = cart;
        this.menu = menu;
        cart.getCartItems().add(this);
    }

    public void updateQuantity(long quantity) {
        if(quantity < 1) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }
}
