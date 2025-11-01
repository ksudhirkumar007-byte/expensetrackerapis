package com.expensetracker.service;

import com.expensetracker.dto.AnalyticsDTO;
import com.expensetracker.dto.ExpenseDTO;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    ExpenseDTO createExpense(ExpenseDTO expenseDTO);

    ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO);

    void deleteExpense(Long id);

    ExpenseDTO getExpenseById(Long id);

    List<ExpenseDTO> getAllExpenses();

    List<ExpenseDTO> getExpensesByCategory(long category);

    List<ExpenseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate);

    AnalyticsDTO getAnalytics();
}