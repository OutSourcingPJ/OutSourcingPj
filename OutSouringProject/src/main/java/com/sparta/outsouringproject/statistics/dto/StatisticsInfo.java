package com.sparta.outsouringproject.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class StatisticsInfo {
    private long totalSalesAmount;
    private long totalOrderCount;
}
