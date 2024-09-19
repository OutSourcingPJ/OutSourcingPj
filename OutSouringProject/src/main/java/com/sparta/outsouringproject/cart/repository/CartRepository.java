package com.sparta.outsouringproject.cart.repository;

import com.sparta.outsouringproject.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
