package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.ProductDto;
import com.data.project_web_service.model.dto.request.UpdateProductDto;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.entity.Product;

public interface ProductService {
    Product getProductById(Integer id);
    Product createProduct(ProductDto productDto);
    Product updateProduct(Integer id, UpdateProductDto updateProductDto);
    void deleteProduct(Integer id);
    PagedResponse<Product> getAllProducts(int page, int size);
}
