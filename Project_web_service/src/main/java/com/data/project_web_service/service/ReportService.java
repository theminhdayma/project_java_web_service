package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.InventoryProductDto;
import com.data.project_web_service.model.dto.request.RevenueReportDto;
import com.data.project_web_service.model.dto.request.SalesSummaryDto;
import com.data.project_web_service.model.dto.request.TopProductDto;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    SalesSummaryDto getSalesSummary(String period);
    List<TopProductDto> getTopProducts(int limit);
    RevenueReportDto getRevenueInPeriod(LocalDate start, LocalDate end);
    List<InventoryProductDto> getInventoryReport();
}
