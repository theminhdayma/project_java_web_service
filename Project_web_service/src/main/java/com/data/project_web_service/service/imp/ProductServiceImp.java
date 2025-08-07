package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.ProductDto;
import com.data.project_web_service.model.dto.request.UpdateProductDto;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.dto.response.Pagination;
import com.data.project_web_service.model.entity.Category;
import com.data.project_web_service.model.entity.Product;
import com.data.project_web_service.repository.CategoryRepository;
import com.data.project_web_service.repository.ProductRepository;
import com.data.project_web_service.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
    }

    @Override
    public Product createProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với id: " + productDto.getCategoryId()));

        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .category(category)
                .isDeleted(false)
                .build();
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Integer id, UpdateProductDto updateProductDto) {
        Product product = getProductById(id);

        Category category = categoryRepository.findById(updateProductDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với id: " + updateProductDto.getCategoryId()));

        product.setName(updateProductDto.getName());
        product.setDescription(updateProductDto.getDescription());
        product.setPrice(updateProductDto.getPrice());
        product.setCategory(category);

        product.setUpdatedAt(LocalDate.now());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        Product product = getProductById(id);

        boolean usedInOrders = productRepository.existsByIdAndOrderItemsIsNotNull(id);

        if (usedInOrders) {
            throw new RuntimeException("Không thể xóa sản phẩm vì nó đã được sử dụng trong đơn hàng.");
        }
        product.setIsDeleted(true);
        product.setUpdatedAt(LocalDate.now());
        productRepository.save(product);
    }

    @Override
    public PagedResponse<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.findAllActiveProducts(pageable);

        Pagination pagination = new Pagination(
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalPages(),
                productPage.getTotalElements()
        );

        return new PagedResponse<>(productPage.getContent(), pagination);
    }
}
