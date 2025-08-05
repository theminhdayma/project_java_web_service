package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.*;
import com.data.project_web_service.model.entity.Invoice;
import com.data.project_web_service.model.entity.Product;
import com.data.project_web_service.repository.InvoiceRepository;
import com.data.project_web_service.repository.OrderRepository;
import com.data.project_web_service.repository.OrderItemRepository;
import com.data.project_web_service.repository.ProductRepository;
import com.data.project_web_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImp implements ReportService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public SalesSummaryDto getSalesSummary(String period) {
        LocalDate now = LocalDate.now();
        LocalDate start;
        switch (period.toLowerCase()) {
            case "week" -> start = now.minusWeeks(1);
            case "month" -> start = now.minusMonths(1);
            case "quarter" -> start = now.minusMonths(3);
            case "year" -> start = now.minusYears(1);
            default -> throw new IllegalArgumentException("Invalid period");
        }
        BigDecimal totalRevenue = invoiceRepository.sumRevenueInPeriod(start, now);
        int totalPaidInvoices = (int) invoiceRepository.findAll().stream()
                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PAID && !i.getCreatedAt().isBefore(start))
                .count();
        int totalOrders = (int) orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(start)).count();
        return new SalesSummaryDto(null, totalRevenue, totalPaidInvoices, totalOrders);
    }

    @Override
    public List<TopProductDto> getTopProducts(int limit) {
        List<Object[]> results = orderItemRepository.topSellingProducts(PageRequest.of(0, limit));
        return results.stream().map(obj -> {
            Product product = (Product) obj[0];
            Long quantitySold = (Long) obj[1];
            return new TopProductDto(product.getId(), product.getName(), quantitySold);
        }).collect(Collectors.toList());
    }

    @Override
    public RevenueReportDto getRevenueInPeriod(LocalDate start, LocalDate end) {
        List<Object[]> revenues = invoiceRepository.revenueByMonth(start, end);
        List<RevenueByTime> dtos = revenues.stream()
                .map(obj -> new RevenueByTime("Tháng " + obj[0], (BigDecimal) obj[1]))
                .collect(Collectors.toList());
        return new RevenueReportDto(dtos);
    }

    @Override
    public List<InventoryProductDto> getInventoryReport() {
        return productRepository.findAll().stream()
                .filter(p -> !p.getIsDeleted())
                .map(p -> new InventoryProductDto(p.getId(), p.getName(), p.getStock()))
                .collect(Collectors.toList());
    }
}
