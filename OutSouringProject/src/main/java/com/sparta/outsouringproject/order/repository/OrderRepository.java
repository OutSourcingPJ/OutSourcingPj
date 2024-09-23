package com.sparta.outsouringproject.order.repository;

import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.order.entity.OrderItem;
import com.sparta.outsouringproject.store.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    default Order findByIdOrElseThrow(long id) {
        return findById(id).orElseThrow(() -> new InvalidRequestException("존재하지 않는 주문입니다."));
    }

    @Query("SELECT oi FROM Order o JOIN o.items oi WHERE o.store = :store")
    List<OrderItem> findAllOrderItemByStore(@Param("store") Store store);

    @Query("SELECT oi FROM OrderItem oi " +
        "JOIN FETCH oi.order o " +
        "JOIN FETCH o.store s " +
        "JOIN FETCH o.user u " +
        "JOIN FETCH oi.menu m " +
        "WHERE oi.order = :order")
    List<OrderItem> findAllOrderItemsByOrder(@Param("order") Order order);
}
