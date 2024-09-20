package com.sparta.outsouringproject.statistics.controller;

import com.sparta.outsouringproject.statistics.dto.StatisticsInfo;
import com.sparta.outsouringproject.statistics.service.StatisticsService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

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

        model.addAttribute("storeId", storeId);
        model.addAttribute("storeName", storeName);
        model.addAttribute("dailySalesAmount", dailySalesAmount != null ? dailySalesAmount : 0L);
        model.addAttribute("dailyOrderCount", dailyOrderCount != null ? dailyOrderCount : 0L);
        model.addAttribute("monthlySalesAmount", monthlySalesAmount != null ? monthlySalesAmount : 0L);
        model.addAttribute("monthlyOrderCount", monthlyOrderCount != null ? monthlyOrderCount : 0L);

        return "storeDashboard";
    }

    @GetMapping("/api/statistics/stores")
    @ResponseBody
    private ResponseEntity<StatisticsInfo> getStatisticStore(@RequestParam Long storeId, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        return ResponseEntity.ok(statisticsService.getStatistics(storeId, startDate, endDate));
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    private ResponseEntity<StatisticsInfo> getStatistic(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        return ResponseEntity.ok(statisticsService.getStatistics(startDate, endDate));
    }
}
