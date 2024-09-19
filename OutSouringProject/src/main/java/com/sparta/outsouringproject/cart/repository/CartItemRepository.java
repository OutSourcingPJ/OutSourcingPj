package com.sparta.outsouringproject.cart.repository;

import com.sparta.outsouringproject.cart.entity.Cart;
import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    default CartItem findCartItemByIdOrElseThrow(Long cartItemId) {
        return findById(cartItemId).orElseThrow(()->new IllegalArgumentException("CartItem not found"));
    }


    Optional<CartItem> findByCart(Cart cart);

    void deleteAllByCart_User(User user);
    List<CartItem> findAllByCart_User(User user);
}
