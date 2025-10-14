package com.expensetracker.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDTO {

    private Double totalExpenses;
    private Double averageDailySpending;
    private Integer totalTransactions;
    private Map<String, Double> categoryWiseSpending;
    private Map<String, Double> dayWiseSpending;
    private CategoryStat topCategory;
    private DayStat highestSpendingDay;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStat {
        private String category;
        private Double amount;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayStat {
        private String date;
        private Double amount;
    }
}