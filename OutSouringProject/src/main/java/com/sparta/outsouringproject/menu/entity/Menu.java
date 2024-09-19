package com.sparta.outsouringproject.menu.entity;

import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long menu_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id",nullable = false)
    private Store store;

    @OneToOne(mappedBy = "menu")
    private CartItem cartItem;
}
