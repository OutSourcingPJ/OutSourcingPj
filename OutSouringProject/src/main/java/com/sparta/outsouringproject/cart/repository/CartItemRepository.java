package com.sparta.outsouringproject.cart.repository;

import com.sparta.outsouringproject.cart.entity.CartItem;
import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    default CartItem findCartItemByIdOrElseThrow(Long cartItemId) {
        return findById(cartItemId).orElseThrow(()->new InvalidRequestException("장바구니에 존재하지 않는 상품입니다."));
    }

    void deleteAllByCart_User(User user);

    @Query("SELECT ci  FROM CartItem ci JOIN FETCH ci.cart c JOIN FETCH c.user u JOIN FETCH ci.menu m JOIN FETCH m.store  WHERE u = :user")
    List<CartItem> findAllByCart_User(@Param("user") User user);
}
