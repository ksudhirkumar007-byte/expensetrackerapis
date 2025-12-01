package com.expensetracker.client;

import com.expensetracker.dto.CategoryDTO;
import feign.Param;
import feign.RequestLine;
import java.util.List;

public interface CategoryClient {

    @RequestLine("GET /api/categories/{id}")
    CategoryDTO getCategoryById(@Param("id") Long id);

    @RequestLine("GET /api/categories")
    List<CategoryDTO> getAllCategories();
}