package com.data.project_web_service.model.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByTime {
    private String timeLabel;
    private BigDecimal totalRevenue;
}
