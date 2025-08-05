package com.data.project_web_service.model.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesSummaryDto {
    private BigDecimal totalSales;
    private BigDecimal totalRevenue;
    private int totalPaidInvoices;
    private int totalOrders;
}
