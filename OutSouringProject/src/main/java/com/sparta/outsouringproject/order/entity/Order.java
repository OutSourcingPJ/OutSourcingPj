package com.sparta.outsouringproject.order.entity;

import com.sparta.outsouringproject.common.enums.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    // @ManyToOne(~~)
    // @JoinColumn(name ="user_id")
    // private User user;

    // @ManyToOne(~~)
    // @JoinColumn(name ="store_id")
    // private Store store;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;

    @Builder
    public Order(/*User user, Store store*/OrderStatus status, LocalDateTime createdAt) {
        // this.user = user;
        // this.store = store;
        this.status = status;
        this.createdAt = createdAt;
    }

}
