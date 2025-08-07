package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.request.InventoryProductDto;
import com.data.project_web_service.model.dto.request.RevenueReportDto;
import com.data.project_web_service.model.dto.request.SalesSummaryDto;
import com.data.project_web_service.model.dto.request.TopProductDto;
import com.data.project_web_service.model.dto.response.*;
import com.data.project_web_service.service.ReportService;
import com.data.project_web_service.sercurity.principal.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/sales-summary")
    public ResponseEntity<APIResponse<SalesSummaryDto>> getSalesSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "month") String period) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, java.time.LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền xem báo cáo", null, HttpStatus.FORBIDDEN, null, java.time.LocalDateTime.now()));
        }
        SalesSummaryDto summary = reportService.getSalesSummary(period);
        return ResponseEntity.ok(new APIResponse<>(true, "Thành công", summary, HttpStatus.OK, null, java.time.LocalDateTime.now()));
    }

    @GetMapping("/top-products")
    public ResponseEntity<APIResponse<List<TopProductDto>>> getTopProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "5") int limit) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, java.time.LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền xem báo cáo", null, HttpStatus.FORBIDDEN, null, java.time.LocalDateTime.now()));
        }
        List<TopProductDto> list = reportService.getTopProducts(limit);
        return ResponseEntity.ok(new APIResponse<>(true, "Thành công", list, HttpStatus.OK, null, java.time.LocalDateTime.now()));
    }

    @GetMapping("/revenue")
    public ResponseEntity<APIResponse<RevenueReportDto>> getRevenue(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, java.time.LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền xem báo cáo", null, HttpStatus.FORBIDDEN, null, java.time.LocalDateTime.now()));
        }
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        RevenueReportDto report = reportService.getRevenueInPeriod(start, end);
        return ResponseEntity.ok(new APIResponse<>(true, "Thành công", report, HttpStatus.OK, null, java.time.LocalDateTime.now()));
    }

    @GetMapping("/inventory")
    public ResponseEntity<APIResponse<List<InventoryProductDto>>> getInventory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, java.time.LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền xem báo cáo", null, HttpStatus.FORBIDDEN, null, java.time.LocalDateTime.now()));
        }
        List<InventoryProductDto> result = reportService.getInventoryReport();
        return ResponseEntity.ok(new APIResponse<>(true, "Thành công", result, HttpStatus.OK, null, java.time.LocalDateTime.now()));
    }
}
