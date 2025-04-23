package com.xalts.expense_tracker.service;

import com.xalts.expense_tracker.dto.ExpenseDTO;
import com.xalts.expense_tracker.dto.MonthlyReportDTO;
import com.xalts.expense_tracker.entity.Expense;
import com.xalts.expense_tracker.entity.User;
import com.xalts.expense_tracker.repository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public ExpenseService(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    public Expense createExpense(ExpenseDTO expenseDTO) {
        User user = userService.getCurrentUser();
        Expense expense = new Expense();
        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        expense.setCategory(expenseDTO.getCategory());
        expense.setDate(expenseDTO.getDate() != null ? expenseDTO.getDate() : LocalDate.now());
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public Page<Expense> getExpenses(Pageable pageable) {
        User user = userService.getCurrentUser();
        return expenseRepository.findByUser(user, pageable);
    }

    public Expense updateExpense(Long id, ExpenseDTO expenseDTO) {
        User user = userService.getCurrentUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to expense");
        }
        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        expense.setCategory(expenseDTO.getCategory());
        expense.setDate(expenseDTO.getDate() != null ? expenseDTO.getDate() : LocalDate.now());
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        User user = userService.getCurrentUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to expense");
        }
        expenseRepository.deleteById(id);
    }


    public double getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        User user = userService.getCurrentUser();
        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getExpensesByCategory(LocalDate startDate, LocalDate endDate) {
        User user = userService.getCurrentUser();
        List<Object[]> results = expenseRepository.findTotalByCategoryForUser(user, startDate, endDate);
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Object[] result : results) {
            categoryTotals.put((String) result[0], ((Number) result[1]).doubleValue());
        }
        return categoryTotals;
    }

    public MonthlyReportDTO getMonthlyReport(int year, int month) {
        User user = userService.getCurrentUser();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        double totalExpenses = getTotalExpenses(startDate, endDate);
        Map<String, Double> expensesByCategory = getExpensesByCategory(startDate, endDate);
        MonthlyReportDTO report = new MonthlyReportDTO();
        report.setTotalExpenses(totalExpenses);
        report.setExpensesByCategory(expensesByCategory);
        return report;
    }
}
