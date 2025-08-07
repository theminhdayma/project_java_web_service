package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.request.InvoiceDto;
import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.entity.Invoice;
import com.data.project_web_service.service.InvoiceService;
import com.data.project_web_service.sercurity.principal.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<APIResponse<PagedResponse<Invoice>>> getInvoices(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền truy cập danh sách hóa đơn", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        PagedResponse<Invoice> invoices = invoiceService.getInvoices(PageRequest.of(page, size));
        return ResponseEntity.ok(new APIResponse<>(true, "Lấy danh sách hóa đơn thành công", invoices, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Invoice>> getInvoiceDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền truy cập chi tiết hóa đơn", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        Invoice invoice = invoiceService.getInvoiceDetail(id);
        return ResponseEntity.ok(new APIResponse<>(true, "Chi tiết hóa đơn", invoice, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @PostMapping
    public ResponseEntity<APIResponse<Invoice>> createInvoice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody InvoiceDto dto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền tạo hóa đơn", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        Invoice invoice = invoiceService.createInvoice(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Tạo hóa đơn thành công", invoice, HttpStatus.CREATED, null, LocalDateTime.now()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<APIResponse<Invoice>> updateInvoiceStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam Invoice.InvoiceStatus status) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền cập nhật hóa đơn", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        Invoice invoice = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(new APIResponse<>(true, "Cập nhật trạng thái hóa đơn thành công", invoice, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<APIResponse<Invoice>> getInvoiceByOrderId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer orderId) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền truy cập hóa đơn theo order", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(new APIResponse<>(true, "Lấy hóa đơn theo order thành công", invoice, HttpStatus.OK, null, LocalDateTime.now()));
    }

}
