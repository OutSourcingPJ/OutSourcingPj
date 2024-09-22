package com.sparta.outsouringproject.user.entity;

//import com.sparta.outsouringproject.cart.entity.Cart;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.order.entity.Order;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이메일로 사용자 ID 설정

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private LocalDateTime signupTime;

    @Enumerated(EnumType.STRING)
    private Role role; // USER 또는 OWNER

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @Column
    private boolean isDeleted = false; // 탈퇴 여부

    public User(String password, String username, String email, Role role, boolean isDeleted) {
        this.password = password;
        this.username = username;
        this.email = email;
        this.signupTime = LocalDateTime.now();
        this.role = role;
        this.isDeleted = isDeleted;
    }
}