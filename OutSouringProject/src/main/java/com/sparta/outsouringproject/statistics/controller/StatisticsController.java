package com.sparta.outsouringproject.statistics.controller;

import com.sparta.outsouringproject.common.dto.ResponseDto;
import com.sparta.outsouringproject.statistics.dto.StatisticsInfo;
import com.sparta.outsouringproject.statistics.service.StatisticsService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 전체 통계 정보 페이지
     * @param model 페이지에 보낼 정보들
     * @return statistics 페이지
     */
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

    /**
     * 특정 가게 대쉬보드 Controller
     * @param model 페이지에 보낼 정보들
     * @param storeId 조회할 가게
     * @return storeDashboard 페이지
     */
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

    /**
     * 해당 가게의 매출, 주문 건 통계
     * @param storeId 조회할 가게
     * @param startDate 통계 시작일
     * @param endDate 통계 종료일
     * @return 해당 가게의 지정 기간동안 매출액, 주문 건
     */
    @GetMapping("/api/statistics/stores")
    @ResponseBody
    private ResponseEntity<ResponseDto<StatisticsInfo>> getStatisticStore(@RequestParam Long storeId, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, statisticsService.getStatistics(storeId, startDate, endDate)));
    }

    /**
     * 전체 매출, 주문 건 통계
     * @param startDate 통계 시작일
     * @param endDate 통계 종료일
     * @return 모든 가게의 지정 기간동안 매출액, 주문 건
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    private ResponseEntity<ResponseDto<StatisticsInfo>> getStatistic(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        StatisticsInfo statistics = statisticsService.getStatistics(startDate, endDate);
        return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, statisticsService.getStatistics(startDate, endDate)));
    }
}
