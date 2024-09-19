package com.sparta.outsouringproject.statistics.service;

public interface StatisticsService {

    /**
     * 일일 매출액
     * @return
     */
    Long getDailySalesAmount();

    /**
     * 일일 주문 건
     * @return
     */
    Long getDailyOrderCount();

    /**
     * 월간 매출액
     * @return
     */
    Long getMonthlySalesAmount();

    /**
     * 월간 주문 건
     * @return
     */
    Long getMonthlyOrderCount();

    /**
     * 특정 가게 일일 매출액
     * @return
     */
    Long getDailySalesAmount(Long storeId);

    /**
     * 특정 가게 일일 주문 건
     * @return
     */
    Long getDailyOrderCount(Long storeId);

    /**
     * 특정 가게 월간 매출액
     * @return
     */
    Long getMonthlySalesAmount(Long storeId);

    /**
     * 특정 가게 월간 주문 건
     * @return
     */
    Long getMonthlyOrderCount(Long storeId);

    String getStoreName(Long storeId);
}
