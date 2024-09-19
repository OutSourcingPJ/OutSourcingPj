package com.sparta.outsouringproject.store.entity;

import com.sparta.outsouringproject.user.entity.*;
import jakarta.persistence.*;
import lombok.*;


import java.awt.*;
import java.time.LocalTime;


@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;
    private String name;
    private Double orderAmount;
    private LocalTime openTime;
    private LocalTime closeTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
