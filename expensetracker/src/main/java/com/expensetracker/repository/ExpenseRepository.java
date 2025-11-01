package com.expensetracker.repository;

import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategory(Category category);

    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category")
    List<Object[]> getCategoryWiseSpending();

    @Query("SELECT e.date, SUM(e.amount) FROM Expense e GROUP BY e.date ORDER BY e.date DESC")
    List<Object[]> getDayWiseSpending();

    @Query("SELECT SUM(e.amount) FROM Expense e")
    Double getTotalExpenses();

    @Query("SELECT COUNT(DISTINCT e.date) FROM Expense e")
    Long getUniqueDaysCount();
}
