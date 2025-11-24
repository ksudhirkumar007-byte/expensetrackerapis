package com.expensetracker.service;

import com.expensetracker.client.CategoryClient;
import com.expensetracker.dto.AnalyticsDTO;
import com.expensetracker.dto.CategoryDTO;
import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryClient categoryClient;
    @Override
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        CategoryDTO catById = categoryClient.getCategoryById(expenseDTO.getCategory_id());
       Category category = convertToEntity(catById);
        Expense expense = convertToEntity(expenseDTO,category);
        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    @Override
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        CategoryDTO catById = categoryClient.getCategoryById(expenseDTO.getCategory_id());
        Category cat =convertToEntity(catById);
        expense.setAmount(expenseDTO.getAmount());
        expense.setCategory(cat);
        expense.setDescription(expenseDTO.getDescription());
        expense.setDate(expenseDTO.getDate());

        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDTO(updatedExpense);
    }

    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        return convertToDTO(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByCategory(long category) {
        CategoryDTO categoryById = categoryClient.getCategoryById(category);
        Category category1 = convertToEntity(categoryById);
        return expenseRepository.findByCategory(category1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByMonth(String month) {
        return expenseRepository.findByMonth(month).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDTO getAnalytics() {
        Double totalExpenses = expenseRepository.getTotalExpenses();
        if (totalExpenses == null) totalExpenses = 0.0;

        Long uniqueDays = expenseRepository.getUniqueDaysCount();
        Double avgDaily = uniqueDays > 0 ? totalExpenses / uniqueDays : 0.0;

        Integer totalTransactions = (int) expenseRepository.count();

        // Category-wise spending
        List<Object[]> categoryData = expenseRepository.getCategoryWiseSpending();
        Map<String, Double> categoryWise = new LinkedHashMap<>();
        for (Object[] row : categoryData) {
            categoryWise.put((String) row[0], (Double) row[1]);
        }

        // Day-wise spending
        List<Object[]> dayData = expenseRepository.getDayWiseSpending();
        Map<String, Double> dayWise = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        for (Object[] row : dayData) {
            LocalDate date = (LocalDate) row[0];
            dayWise.put(date.format(formatter), (Double) row[1]);
        }

        // Top category
        Double finalTotalExpenses = totalExpenses;
        AnalyticsDTO.CategoryStat topCategory = categoryWise.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new AnalyticsDTO.CategoryStat(
                        e.getKey(),
                        e.getValue(),
                        (e.getValue() / finalTotalExpenses) * 100
                ))
                .orElse(new AnalyticsDTO.CategoryStat("N/A", 0.0, 0.0));

        // Highest spending day
        AnalyticsDTO.DayStat highestDay = dayWise.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new AnalyticsDTO.DayStat(e.getKey(), e.getValue()))
                .orElse(new AnalyticsDTO.DayStat("N/A", 0.0));

        return AnalyticsDTO.builder()
                .totalExpenses(totalExpenses)
                .averageDailySpending(avgDaily)
                .totalTransactions(totalTransactions)
                .categoryWiseSpending(categoryWise)
                .dayWiseSpending(dayWise)
                .topCategory(topCategory)
                .highestSpendingDay(highestDay)
                .build();
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .category_id(expense.getCategory().getId())
                .description(expense.getDescription())
                .date(expense.getDate())
                .build();
    }


    private Expense convertToEntity(ExpenseDTO dto,Category category) {
        return Expense.builder()
                .amount(dto.getAmount())
                .category(category)
                .description(dto.getDescription())
                .date(dto.getDate())
                .build();
    }
    private Category convertToEntity(CategoryDTO category) {
        return Category.builder().id(category.getId()).type(category.getType()).name(category.getName()).budget(category.getBudget()).build();
    }
}