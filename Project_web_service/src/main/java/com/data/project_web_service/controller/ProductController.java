package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.request.ProductDto;
import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.entity.Product;
import com.data.project_web_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<APIResponse<PagedResponse<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search
    ) {
        PagedResponse<Product> products = productService.getAllProducts(page, size, categoryId, search);
        APIResponse<PagedResponse<Product>> response = new APIResponse<>(
                true,
                "Lấy danh sách sản phẩm thành công",
                products,
                HttpStatus.OK,
                null,
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Product>> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        APIResponse<Product> response = new APIResponse<>(
                true,
                "Lấy sản phẩm thành công",
                product,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<APIResponse<Product>> createProduct(@Valid @RequestBody ProductDto productDto) {
        Product created = productService.createProduct(productDto);
        APIResponse<Product> response = new APIResponse<>(
                true,
                "Tạo sản phẩm thành công",
                created,
                HttpStatus.CREATED,
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Product>> updateProduct(@PathVariable Integer id,
                                                              @Valid @RequestBody ProductDto productDto) {
        Product updated = productService.updateProduct(id, productDto);
        APIResponse<Product> response = new APIResponse<>(
                true,
                "Cập nhật sản phẩm thành công",
                updated,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        APIResponse<Void> response = new APIResponse<>(
                true,
                "Xóa sản phẩm thành công",
                null,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
}
