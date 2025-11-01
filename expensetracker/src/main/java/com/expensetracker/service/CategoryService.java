package com.expensetracker.service;

import com.expensetracker.dto.CategoryDTO;
import com.expensetracker.dto.ExpenseDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> getAllCategories();
}
