package com.xalts.expense_tracker.facade.impl;

import com.xalts.expense_tracker.dto.ExpenseDTO;
import com.xalts.expense_tracker.dto.LoginRequest;
import com.xalts.expense_tracker.dto.MonthlyReportDTO;
import com.xalts.expense_tracker.dto.RegisterRequest;
import com.xalts.expense_tracker.facade.ExpenseTrackerFacade;
import com.xalts.expense_tracker.entity.Expense;
import com.xalts.expense_tracker.entity.User;
import com.xalts.expense_tracker.service.ExpenseService;
import com.xalts.expense_tracker.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class ExpenseTrackerFacadeImpl implements ExpenseTrackerFacade {

    private final UserService userService;
    private final ExpenseService expenseService;

    public ExpenseTrackerFacadeImpl(UserService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @Override
    public User registerUser(RegisterRequest request) {
        return userService.registerUser(request);
    }

    @Override
    public String loginUser(LoginRequest request) {
        return userService.loginUser(request);
    }

    @Override
    public Expense createExpense(ExpenseDTO expenseDTO) {
        return expenseService.createExpense(expenseDTO);
    }

    @Override
    public Page<Expense> getExpenses(Pageable pageable) {
        return expenseService.getExpenses(pageable);
    }

    @Override
    public Expense updateExpense(Long id, ExpenseDTO expenseDTO) {
        return expenseService.updateExpense(id, expenseDTO);
    }

    @Override
    public void deleteExpense(Long id) {
        expenseService.deleteExpense(id);
    }

    @Override
    public double getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        return expenseService.getTotalExpenses(startDate, endDate);
    }

    @Override
    public Map<String, Double> getExpensesByCategory(LocalDate startDate, LocalDate endDate) {
        return expenseService.getExpensesByCategory(startDate, endDate);
    }

    @Override
    public MonthlyReportDTO getMonthlyReport(int year, int month) {
        return expenseService.getMonthlyReport(year, month);
    }
}
