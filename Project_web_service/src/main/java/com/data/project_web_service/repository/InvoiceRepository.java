package com.data.project_web_service.repository;

import com.data.project_web_service.model.entity.Invoice;
import com.data.project_web_service.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Page<Invoice> findAll(Pageable pageable);

    Page<Invoice> findByOrder(Order order, Pageable pageable);

    Invoice findByOrder(Order order);

    // Báo cáo tổng quan doanh số theo thời gian
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Doanh thu nhóm theo tuần/tháng/quý (ví dụ lấy tổng theo tháng)
    @Query("SELECT FUNCTION('MONTH', i.createdAt) as month, SUM(i.totalAmount) as total " +
            "FROM Invoice i WHERE i.status = 'PAID' AND i.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('MONTH', i.createdAt)")
    List<Object[]> revenueByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
