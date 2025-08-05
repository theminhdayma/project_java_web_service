package com.data.project_web_service.model.dto.request;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueReportDto {
    private List<RevenueByTime> revenues;
}
