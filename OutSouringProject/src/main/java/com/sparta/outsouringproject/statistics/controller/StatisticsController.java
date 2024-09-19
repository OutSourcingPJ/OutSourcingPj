package com.sparta.outsouringproject.statistics.controller;

import com.sparta.outsouringproject.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/statistics")
    private String getStatistics(Model model){
        Long dailySalesAmount = statisticsService.getDailySalesAmount();
        Long dailyOrderCount = statisticsService.getDailyOrderCount();
        Long monthlySalesAmount = statisticsService.getMonthlySalesAmount();
        Long monthlyOrderCount = statisticsService.getMonthlyOrderCount();

        model.addAttribute("dailySalesAmount", dailySalesAmount != null ? dailySalesAmount : 0L);
        model.addAttribute("dailyOrderCount", dailyOrderCount != null ? dailyOrderCount : 0L);
        model.addAttribute("monthlySalesAmount", monthlySalesAmount != null ? monthlySalesAmount : 0L);
        model.addAttribute("monthlyOrderCount", monthlyOrderCount != null ? monthlyOrderCount : 0L);

        return "statistics";
    }

    @GetMapping("/statistics/stores/{storeId}")
    private String getStatistics(Model model, @PathVariable("storeId") Long storeId){
        Long dailySalesAmount = statisticsService.getDailySalesAmount(storeId);
        Long dailyOrderCount = statisticsService.getDailyOrderCount(storeId);
        Long monthlySalesAmount = statisticsService.getMonthlySalesAmount(storeId);
        Long monthlyOrderCount = statisticsService.getMonthlyOrderCount(storeId);
        String storeName = statisticsService.getStoreName(storeId);

        model.addAttribute("storeName", storeName);
        model.addAttribute("dailySalesAmount", dailySalesAmount != null ? dailySalesAmount : 0L);
        model.addAttribute("dailyOrderCount", dailyOrderCount != null ? dailyOrderCount : 0L);
        model.addAttribute("monthlySalesAmount", monthlySalesAmount != null ? monthlySalesAmount : 0L);
        model.addAttribute("monthlyOrderCount", monthlyOrderCount != null ? monthlyOrderCount : 0L);

        return "storeDashboard";
    }
}
