package com.sparta.outsouringproject.order.repository;

import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.order.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi " +
        "JOIN FETCH oi.order o " +
        "JOIN FETCH o.store s " +
        "JOIN FETCH o.user u " +
        "JOIN FETCH oi.menu m " +
        "WHERE oi.order = :order")
    List<OrderItem> findAllByOrder(@Param("order") Order order);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH  oi.order WHERE oi.order IN :order ORDER BY  oi.order.id")
    List<OrderItem> findAllByOrderIn(List<Order> order);
}
