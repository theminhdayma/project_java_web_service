package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.ProductDto;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.entity.Product;

public interface ProductService {
    Product getProductById(Integer id);
    Product createProduct(ProductDto productDto);
    Product updateProduct(Integer id, ProductDto productDto);
    void deleteProduct(Integer id);
    PagedResponse<Product> getAllProducts(int page, int size, Integer categoryId, String search);
}
