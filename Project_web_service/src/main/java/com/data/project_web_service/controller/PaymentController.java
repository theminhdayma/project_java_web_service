package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.request.PaymentDto;
import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.model.entity.Payment;
import com.data.project_web_service.service.PaymentService;
import com.data.project_web_service.sercurity.principal.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<APIResponse<Payment>> createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentDto paymentDto
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now())
            );
        }

        Payment payment = paymentService.createPayment(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new APIResponse<>(true, "Thanh toán thành công", payment, HttpStatus.CREATED, null, LocalDateTime.now())
        );
        }

        // GET /api/v1/payments/{id} : Lấy thông tin chi tiết payment (ADMIN, CUSTOMER, SALES)
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Payment>> getPaymentById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now())
            );
        }

        // Có thể bổ sung kiểm tra quyền/ownership nếu business cần
        Payment payment = paymentService.getPaymentDetail(id);
        return ResponseEntity.ok(new APIResponse<>(true, "Lấy thông tin thanh toán thành công", payment, HttpStatus.OK, null, LocalDateTime.now()));
    }
}
