package com.sparta.outsouringproject.store.entity;

import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.store.dto.CreateStoreRequestDto;
import com.sparta.outsouringproject.user.entity.User;
import com.sparta.outsouringproject.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;
    @Column(length = 50)
    private String name;
    @Column(length = 50)
    private Double orderAmount;
    @Column(length = 50)
    private LocalTime openTime;
    @Column(length = 50)
    private LocalTime closeTime;
    private Boolean storeStatus;
    private Boolean advertise;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Menu> menuList = new ArrayList<>();

    public Store(CreateStoreRequestDto requestDto, User user) {
        this.name = requestDto.getName();
        this.orderAmount = requestDto.getOrderAmount();
        this.openTime = requestDto.getOpenTime();
        this.closeTime = requestDto.getCloseTime();
        this.storeStatus = false;
        this.advertise = false;
        this.user = user;
    }
    public void changeName(String name) {
        this.name = name;
    }
    public void changeAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }
    public void changeOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }
    public void changeCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }
    public void deleteStore() {
        this.storeStatus = true;
    }
    public void checkAdvertise() {
        this.advertise = true;
    }
    public void unCheckAdvertise() {
        this.advertise = false;
    }
}
