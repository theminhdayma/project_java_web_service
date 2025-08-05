package com.data.project_web_service.repository;

import com.data.project_web_service.model.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrderId(Integer orderId);

    // Báo cáo top sản phẩm bán chạy
    @Query("SELECT oi.product, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi WHERE oi.order.status = 'CONFIRMED' OR oi.order.status = 'SHIPPED' " +
            "GROUP BY oi.product ORDER BY totalSold DESC")
    List<Object[]> topSellingProducts(Pageable pageable);
}
