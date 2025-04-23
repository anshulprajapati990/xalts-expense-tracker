package com.xalts.expense_tracker.controller;

import com.xalts.expense_tracker.dto.ExpenseDTO;
import com.xalts.expense_tracker.dto.MonthlyReportDTO;
import com.xalts.expense_tracker.facade.ExpenseTrackerFacade;
import com.xalts.expense_tracker.entity.Expense;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseTrackerFacade facade;

    public ExpenseController(ExpenseTrackerFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        Expense expense = facade.createExpense(expenseDTO);
        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public ResponseEntity<Page<Expense>> getExpenses(Pageable pageable) {
        Page<Expense> expenses = facade.getExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO) {
        Expense expense = facade.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        facade.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalExpenses(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        double total = facade.getTotalExpenses(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/by-category")
    public ResponseEntity<Map<String, Double>> getExpensesByCategory(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Double> categoryTotals = facade.getExpensesByCategory(startDate, endDate);
        return ResponseEntity.ok(categoryTotals);
    }

    @GetMapping("/report/monthly")
    public ResponseEntity<MonthlyReportDTO> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        MonthlyReportDTO report = facade.getMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }
}