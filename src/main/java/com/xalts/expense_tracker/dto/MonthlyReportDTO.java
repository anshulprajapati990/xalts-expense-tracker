package com.xalts.expense_tracker.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MonthlyReportDTO {
    private double totalExpenses;
    private Map<String, Double> expensesByCategory;
}
