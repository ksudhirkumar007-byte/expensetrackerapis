package com.expensetracker.client;

import com.expensetracker.dto.CategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "category-service", url = "http://xpenss.in")  // Use real URL or Discovery service name
public interface CategoryClient {

    @GetMapping("/api/categories/{id}")
    CategoryDTO getCategoryById(@PathVariable("id") Long id);

    @GetMapping("/api/categories")
    List<CategoryDTO> getAllCategories();
}