package com.sparta.outsouringproject.statistics.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime soldDate;
    private Long storeId;
    private Long orderId;
    private Long userId;
    private Long menuId;
    private String menuName;
    private Long quantity;
    private Long soldPrice;
    private Long soldTotalPrice;

    @Builder
    public OrderHistory(LocalDateTime soldDate, Long storeId, Long orderId, Long userId,
        Long menuId, String menuName, Long quantity, Long soldPrice, Long soldTotalPrice) {
        this.soldDate = soldDate;
        this.storeId = storeId;
        this.orderId = orderId;
        this.userId = userId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.quantity = quantity;
        this.soldPrice = soldPrice;
        this.soldTotalPrice = soldTotalPrice;
    }
}
