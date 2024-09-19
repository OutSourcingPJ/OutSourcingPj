package com.sparta.outsouringproject.order.repository;

import com.sparta.outsouringproject.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
