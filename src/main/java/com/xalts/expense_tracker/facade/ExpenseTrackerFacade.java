package com.xalts.expense_tracker.facade;

import com.xalts.expense_tracker.dto.ExpenseDTO;
import com.xalts.expense_tracker.dto.LoginRequest;
import com.xalts.expense_tracker.dto.MonthlyReportDTO;
import com.xalts.expense_tracker.dto.RegisterRequest;
import com.xalts.expense_tracker.entity.Expense;
import com.xalts.expense_tracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface ExpenseTrackerFacade {
    User registerUser(RegisterRequest request);

    String loginUser(LoginRequest request);
    Expense createExpense(ExpenseDTO expenseDTO);
    Page<Expense> getExpenses(Pageable pageable);
    Expense updateExpense(Long id, ExpenseDTO expenseDTO);
    void deleteExpense(Long id);
    double getTotalExpenses(LocalDate startDate, LocalDate endDate);
    Map<String, Double> getExpensesByCategory(LocalDate startDate, LocalDate endDate);
    MonthlyReportDTO getMonthlyReport(int year, int month);
}
