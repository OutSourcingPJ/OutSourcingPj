package com.sparta.outsouringproject.order.repository;

import com.sparta.outsouringproject.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    default Order findByIdOrElseThrow(long id) {
        return findById(id).orElseThrow(()-> new IllegalStateException("Order not found"));
    }
}
