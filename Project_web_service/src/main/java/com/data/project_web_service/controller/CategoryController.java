package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.request.CategoryDto;
import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.model.entity.Category;
import com.data.project_web_service.service.CategoryService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<APIResponse<Category>> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Category created = categoryService.createCategory(categoryDto);
        APIResponse<Category> response = new APIResponse<>(
                true,
                "Tạo danh mục thành công",
                created,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Category>> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryDto categoryDto) {
        Category updated = categoryService.updateCategory(id, categoryDto);
        APIResponse<Category> response = new APIResponse<>(
                true,
                "Cập nhật danh mục thành công",
                updated,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Category>> getCategoryById(@PathVariable Integer id) {
        Category category = categoryService.getCategoryById(id);
        APIResponse<Category> response = new APIResponse<>(
                true,
                "Lấy danh mục thành công",
                category,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<Category>>> getCategories() {
        List<Category> categories = categoryService.getCategories();
        APIResponse<List<Category>> response = new APIResponse<>(
                true,
                "Lấy danh sách danh mục thành công",
                categories,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        APIResponse<String> response = new APIResponse<>(
                true,
                "Danh mục đã được xóa thành công",
                null,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
}
