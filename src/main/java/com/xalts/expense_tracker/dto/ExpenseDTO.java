package com.xalts.expense_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseDTO {
    @Positive(message = "Amount must be positive")
    private double amount;

    private String description;

    @NotBlank(message = "Category is mandatory")
    private String category;

    private LocalDate date;
}
