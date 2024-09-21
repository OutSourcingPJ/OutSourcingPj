package com.sparta.outsouringproject.order.repository;

import com.sparta.outsouringproject.common.exceptions.InvalidRequestException;
import com.sparta.outsouringproject.order.entity.Order;
import com.sparta.outsouringproject.store.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    default Order findByIdOrElseThrow(long id) {
        return findById(id).orElseThrow(()-> new InvalidRequestException("존재하지 않는 주문입니다."));
    }

    List<Order> findAllByStore(Store store);
}
