package com.sparta.outsouringproject.statistics.service;

import com.sparta.outsouringproject.statistics.dto.StatisticsInfo;
import com.sparta.outsouringproject.statistics.repository.OrderHistoryRepository;
import com.sparta.outsouringproject.store.entity.Store;
import com.sparta.outsouringproject.store.repository.StoreRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final StoreRepository storeRepository;

    @Override
    public Long getDailySalesAmount() {
        LocalDateTime startOfDay = LocalDate.now()
            .atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderHistoryRepository.findDailySalesAmount(startOfDay, endOfDay);
    }

    @Override
    public Long getDailyOrderCount() {
        LocalDateTime startOfDay = LocalDate.now()
            .atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderHistoryRepository.findDailyOrderCount(startOfDay, endOfDay);
    }

    @Override
    public Long getMonthlySalesAmount() {
        LocalDateTime startOfMonth = LocalDate.now()
            .withDayOfMonth(1)
            .atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return orderHistoryRepository.findMonthlySalesAmount(startOfMonth, endOfMonth);
    }

    @Override
    public Long getMonthlyOrderCount() {
        LocalDateTime startOfMonth = LocalDate.now()
            .withDayOfMonth(1)
            .atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return orderHistoryRepository.findMonthlyOrderCount(startOfMonth, endOfMonth);
    }

    @Override
    public Long getDailySalesAmount(Long storeId) {
        LocalDateTime startOfDay = LocalDate.now()
            .atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderHistoryRepository.findDailySalesAmountByStoreId(storeId, startOfDay, endOfDay);
    }

    @Override
    public Long getDailyOrderCount(Long storeId) {
        LocalDateTime startOfDay = LocalDate.now()
            .atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderHistoryRepository.findDailyOrderCountByStoreId(storeId, startOfDay, endOfDay);
    }

    @Override
    public Long getMonthlySalesAmount(Long storeId) {
        LocalDateTime startOfMonth = LocalDate.now()
            .withDayOfMonth(1)
            .atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return orderHistoryRepository.findMonthlySalesAmountByStoreId(storeId, startOfMonth,
            endOfMonth);
    }

    @Override
    public Long getMonthlyOrderCount(Long storeId) {
        LocalDateTime startOfMonth = LocalDate.now()
            .withDayOfMonth(1)
            .atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return orderHistoryRepository.findMonthlyOrderCountByStoreId(storeId, startOfMonth,
            endOfMonth);
    }

    @Override
    public String getStoreName(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));
        return store.getName();
    }

    @Override
    public StatisticsInfo getStatistics(Long storeId, LocalDate startDate, LocalDate endDate) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        long totalSalesAmount = orderHistoryRepository.findSalesAmountByStoreId(storeId,
            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        long totalOrderCount = orderHistoryRepository.findOrderCountByStoreId(storeId,
            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        return new StatisticsInfo(totalSalesAmount, totalOrderCount);
    }

    @Override
    public StatisticsInfo getStatistics(LocalDate startDate, LocalDate endDate) {
        long totalSalesAmount = orderHistoryRepository.findSalesAmount(startDate.atStartOfDay(),
            endDate.atTime(LocalTime.MAX));
        long totalOrderCount = orderHistoryRepository.findOrderCount(startDate.atStartOfDay(),
            endDate.atTime(LocalTime.MAX));

        return new StatisticsInfo(totalSalesAmount, totalOrderCount);
    }

}
