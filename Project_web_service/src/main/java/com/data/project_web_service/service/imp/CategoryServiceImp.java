package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.CategoryDto;
import com.data.project_web_service.model.entity.Category;
import com.data.project_web_service.repository.CategoryRepository;
import com.data.project_web_service.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDto categoryDto) {

        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new RuntimeException("Danh mục với tên '" + categoryDto.getName() + "' đã tồn tại.");
        }

        Category category = Category.builder()
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .isDeleted(false)
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Integer id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với id: " + id));
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new RuntimeException("Danh mục với tên '" + categoryDto.getName() + "' đã tồn tại.");
        }
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với id: " + id));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            category.setIsDeleted(true);
            category.setDeletedAt(LocalDate.now());
            categoryRepository.save(category);
        } else {
            categoryRepository.delete(category);
        }
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với id: " + id));
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll().stream()
                .filter(cat -> !cat.getIsDeleted())
                .toList();
    }
}
