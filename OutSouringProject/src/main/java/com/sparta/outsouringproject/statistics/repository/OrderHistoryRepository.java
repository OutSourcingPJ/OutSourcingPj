package com.sparta.outsouringproject.statistics.repository;

import com.sparta.outsouringproject.statistics.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

}
