package com.sparta.outsouringproject.statistics.repository;

import com.sparta.outsouringproject.statistics.entity.OrderHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    // 일일 매출액
    @Query("SELECT SUM(o.soldTotalPrice) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay")
    Long findDailySalesAmount(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 일일 주문 건수
    @Query("SELECT COUNT(DISTINCT o.orderId) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay")
    Long findDailyOrderCount(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 월간 매출액
    @Query("SELECT SUM(o.soldTotalPrice) FROM OrderHistory o WHERE o.soldDate >= :startOfMonth AND o.soldDate < :endOfMonth")
    Long findMonthlySalesAmount(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    // 월간 주문 건수
    @Query("SELECT COUNT(DISTINCT o.orderId) FROM OrderHistory o WHERE o.soldDate >= :startOfMonth AND o.soldDate < :endOfMonth")
    Long findMonthlyOrderCount(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    // 날짜 범위 주문 기록
    @Query("SELECT o FROM OrderHistory  o WHERE o.soldDate >= :startOfDate AND o.soldDate < :endOfDate")
    List<OrderHistory> findAllDailyOrderHistory(@Param("startOfDate") LocalDateTime startOfDate, @Param("endOfDate") LocalDateTime endOfDate);

    // 특정 가게 날짜 범위 주문 기록
    @Query("SELECT o FROM OrderHistory  o WHERE o.soldDate >= :startOfDate AND o.soldDate < :endOfDate AND o.storeId = :storeId")
    List<OrderHistory> findAllDailyOrderHistoryByStoreId(@Param("storeId") Long storeId, @Param("startOfDate") LocalDateTime startOfDate, @Param("endOfDate") LocalDateTime endOfDate);

    // 범위 매출액
    @Query("SELECT SUM(o.soldTotalPrice) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay")
    Long findSalesAmount(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 범위 주문 건수
    @Query("SELECT COUNT(DISTINCT o.orderId) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay")
    Long findOrderCount(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);


    // 특정 가게 범위 매출액
    @Query("SELECT SUM(o.soldTotalPrice) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay AND o.storeId = :storeId")
    Long findSalesAmountByStoreId(@Param("storeId") Long storeId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 특정 가게 범위 주문 건수
    @Query("SELECT COUNT(DISTINCT o.orderId) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay AND o.storeId = :storeId")
    Long findOrderCountByStoreId(@Param("storeId") Long storeId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);


    // 특정 가게 일일 매출액
    @Query("SELECT SUM(o.soldTotalPrice) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay AND o.storeId = :storeId")
    Long findDailySalesAmountByStoreId(@Param("storeId") Long storeId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 특정 가게 일일 주문 건수
    @Query("SELECT COUNT(DISTINCT o.orderId) FROM OrderHistory o WHERE o.soldDate >= :startOfDay AND o.soldDate < :endOfDay AND o.storeId = :storeId")
    Long findDailyOrderCountByStoreId(@Param("storeId") Long storeId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    // 특정 가게 월간 매출액
    @Query("SELECT SUM(o.soldTotalPrice) FROM OrderHistory o WHERE o.soldDate >= :startOfMonth AND o.soldDate < :endOfMonth AND o.storeId = :storeId")
    Long findMonthlySalesAmountByStoreId(@Param("storeId") Long storeId, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    // 특정 가게 월간 주문 건수
    @Query("SELECT COUNT(DISTINCT o.orderId) FROM OrderHistory o WHERE o.soldDate >= :startOfMonth AND o.soldDate < :endOfMonth AND o.storeId = :storeId")
    Long findMonthlyOrderCountByStoreId(@Param("storeId") Long storeId, @Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);
}
