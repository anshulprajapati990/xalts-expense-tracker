package com.xalts.expense_tracker.service;

import com.xalts.expense_tracker.dto.ExpenseDTO;
import com.xalts.expense_tracker.dto.MonthlyReportDTO;
import com.xalts.expense_tracker.entity.Expense;
import com.xalts.expense_tracker.entity.User;
import com.xalts.expense_tracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseService expenseService;

    private User user;
    private Expense expense;
    private ExpenseDTO expenseDTO;
    private Pageable pageable;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        expense = new Expense();
        expense.setId(1L);
        expense.setAmount(100.0);
        expense.setDescription("Lunch");
        expense.setCategory("Food");
        expense.setDate(LocalDate.of(2025, 4, 22));
        expense.setUser(user);

        expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(100.0);
        expenseDTO.setDescription("Lunch");
        expenseDTO.setCategory("Food");
        expenseDTO.setDate(LocalDate.of(2025, 4, 22));

        pageable = PageRequest.of(0, 10);
        startDate = LocalDate.of(2025, 4, 1);
        endDate = LocalDate.of(2025, 4, 30);
    }

    @Test
    void createExpense_successfulCreation() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        // Act
        Expense result = expenseService.createExpense(expenseDTO);

        // Assert
        assertNotNull(result);
        assertEquals(100.0, result.getAmount());
        assertEquals("Lunch", result.getDescription());
        assertEquals("Food", result.getCategory());
        assertEquals(LocalDate.of(2025, 4, 22), result.getDate());
        assertEquals(user, result.getUser());
        verify(userService).getCurrentUser();
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void createExpense_nullDate_setsCurrentDate() {
        // Arrange
        expenseDTO.setDate(null);
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense savedExpense = invocation.getArgument(0);
            savedExpense.setId(1L);
            return savedExpense;
        });

        // Act
        Expense result = expenseService.createExpense(expenseDTO);

        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getDate());
        verify(userService).getCurrentUser();
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void getExpenses_returnsPageWithExpenses() {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);
        Page<Expense> page = new PageImpl<>(expenses, pageable, expenses.size());
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user, pageable)).thenReturn(page);

        // Act
        Page<Expense> result = expenseService.getExpenses(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(expense, result.getContent().get(0));
        verify(userService).getCurrentUser();
        verify(expenseRepository).findByUser(user, pageable);
    }

    @Test
    void getExpenses_returnsEmptyPage() {
        // Arrange
        Page<Expense> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUser(user, pageable)).thenReturn(emptyPage);

        // Act
        Page<Expense> result = expenseService.getExpenses(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(userService).getCurrentUser();
        verify(expenseRepository).findByUser(user, pageable);
    }

    @Test
    void updateExpense_successfulUpdate() {
        // Arrange
        ExpenseDTO updatedDTO = new ExpenseDTO();
        updatedDTO.setAmount(150.0);
        updatedDTO.setDescription("Dinner");
        updatedDTO.setCategory("Food");
        updatedDTO.setDate(LocalDate.of(2025, 4, 23));

        Expense updatedExpense = new Expense();
        updatedExpense.setId(1L);
        updatedExpense.setAmount(150.0);
        updatedExpense.setDescription("Dinner");
        updatedExpense.setCategory("Food");
        updatedExpense.setDate(LocalDate.of(2025, 4, 23));
        updatedExpense.setUser(user);

        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        // Act
        Expense result = expenseService.updateExpense(1L, updatedDTO);

        // Assert
        assertNotNull(result);
        assertEquals(150.0, result.getAmount());
        assertEquals("Dinner", result.getDescription());
        assertEquals("Food", result.getCategory());
        assertEquals(LocalDate.of(2025, 4, 23), result.getDate());
        verify(userService).getCurrentUser();
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void updateExpense_expenseNotFound_throwsException() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.updateExpense(1L, expenseDTO);
        });
        assertEquals("Expense not found", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(expenseRepository).findById(1L);
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    void updateExpense_unauthorizedUser_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.updateExpense(1L, expenseDTO);
        });
        assertEquals("Unauthorized access to expense", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(expenseRepository).findById(1L);
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    void deleteExpense_successfulDeletion() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        doNothing().when(expenseRepository).deleteById(1L);

        // Act
        expenseService.deleteExpense(1L);

        // Assert
        verify(userService).getCurrentUser();
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void deleteExpense_expenseNotFound_throwsException() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.deleteExpense(1L);
        });
        assertEquals("Expense not found", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(expenseRepository).findById(1L);
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    void deleteExpense_unauthorizedUser_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.deleteExpense(1L);
        });
        assertEquals("Unauthorized access to expense", exception.getMessage());
        verify(userService).getCurrentUser();
        verify(expenseRepository).findById(1L);
        verifyNoMoreInteractions(expenseRepository);
    }

    @Test
    void getTotalExpenses_returnsSum() {
        // Arrange
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setAmount(50.0);
        expense2.setDescription("Taxi");
        expense2.setCategory("Travel");
        expense2.setDate(LocalDate.of(2025, 4, 23));
        expense2.setUser(user);

        List<Expense> expenses = Arrays.asList(expense, expense2);
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUserAndDateBetween(user, startDate, endDate)).thenReturn(expenses);

        // Act
        double result = expenseService.getTotalExpenses(startDate, endDate);

        // Assert
        assertEquals(150.0, result);
        verify(userService).getCurrentUser();
        verify(expenseRepository).findByUserAndDateBetween(user, startDate, endDate);
    }

    @Test
    void getTotalExpenses_emptyList_returnsZero() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUserAndDateBetween(user, startDate, endDate)).thenReturn(Collections.emptyList());

        // Act
        double result = expenseService.getTotalExpenses(startDate, endDate);

        // Assert
        assertEquals(0.0, result);
        verify(userService).getCurrentUser();
        verify(expenseRepository).findByUserAndDateBetween(user, startDate, endDate);
    }

    @Test
    void getExpensesByCategory_returnsCategoryTotals() {
        // Arrange
        List<Object[]> results = Arrays.asList(
                new Object[]{"Food", 100.0},
                new Object[]{"Travel", 50.0}
        );
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findTotalByCategoryForUser(user, startDate, endDate)).thenReturn(results);

        // Act
        Map<String, Double> result = expenseService.getExpensesByCategory(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100.0, result.get("Food"));
        assertEquals(50.0, result.get("Travel"));
        verify(userService).getCurrentUser();
        verify(expenseRepository).findTotalByCategoryForUser(user, startDate, endDate);
    }

    @Test
    void getExpensesByCategory_emptyList_returnsEmptyMap() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findTotalByCategoryForUser(user, startDate, endDate)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Double> result = expenseService.getExpensesByCategory(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService).getCurrentUser();
        verify(expenseRepository).findTotalByCategoryForUser(user, startDate, endDate);
    }

    @Test
    void getMonthlyReport_returnsReport() {
        // Arrange
        List<Expense> expenses = Arrays.asList(expense);
        List<Object[]> categoryResults = Collections.singletonList(new Object[]{"Food", 100.0}); // Fixed to List<Object[]>
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUserAndDateBetween(user, startDate, endDate)).thenReturn(expenses);
        when(expenseRepository.findTotalByCategoryForUser(user, startDate, endDate)).thenReturn(categoryResults);

        // Act
        MonthlyReportDTO result = expenseService.getMonthlyReport(2025, 4);

        // Assert
        assertNotNull(result);
        assertEquals(100.0, result.getTotalExpenses());
        assertNotNull(result.getExpensesByCategory());
        assertEquals(1, result.getExpensesByCategory().size());
        assertEquals(100.0, result.getExpensesByCategory().get("Food"));
        verify(userService, times(3)).getCurrentUser(); // Expect 3 calls
        verify(expenseRepository).findByUserAndDateBetween(user, startDate, endDate);
        verify(expenseRepository).findTotalByCategoryForUser(user, startDate, endDate);
    }

    @Test
    void getMonthlyReport_emptyData_returnsEmptyReport() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findByUserAndDateBetween(user, startDate, endDate)).thenReturn(Collections.emptyList());
        when(expenseRepository.findTotalByCategoryForUser(user, startDate, endDate)).thenReturn(Collections.emptyList());

        // Act
        MonthlyReportDTO result = expenseService.getMonthlyReport(2025, 4);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getTotalExpenses());
        assertNotNull(result.getExpensesByCategory());
        assertTrue(result.getExpensesByCategory().isEmpty());
        verify(userService, times(3)).getCurrentUser(); // Expect 3 calls
        verify(expenseRepository).findByUserAndDateBetween(user, startDate, endDate);
        verify(expenseRepository).findTotalByCategoryForUser(user, startDate, endDate);
    }
}