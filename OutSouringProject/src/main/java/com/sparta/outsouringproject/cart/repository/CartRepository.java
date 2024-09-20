package com.sparta.outsouringproject.cart.repository;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    void deleteByUser(User user);
}
