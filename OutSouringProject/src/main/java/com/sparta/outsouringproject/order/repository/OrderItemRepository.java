package com.sparta.outsouringproject.order.repository;

import com.sparta.outsouringproject.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
