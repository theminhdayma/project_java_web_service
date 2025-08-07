package com.data.project_web_service.repository;

import com.data.project_web_service.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // lấy tất cả sản phẩm có trạng thái là true
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false")
    Page<Product> findAllActiveProducts(Pageable pageable);

    boolean existsByIdAndOrderItemsIsNotNull(Integer productId);
}
