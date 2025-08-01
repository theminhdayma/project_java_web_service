package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.CategoryDto;
import com.data.project_web_service.model.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDto categoryDto);
    Category updateCategory(Integer id, CategoryDto categoryDto);
    void deleteCategory(Integer id);
    Category getCategoryById(Integer id);
    List<Category> getCategories();
}
