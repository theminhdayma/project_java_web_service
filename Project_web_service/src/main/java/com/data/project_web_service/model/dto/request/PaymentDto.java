package com.data.project_web_service.model.dto.request;

import com.data.project_web_service.model.entity.Payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Integer id;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Payment.PaymentMethod method;

    private Payment.PaymentStatus status;

    @NotBlank(message = "Id giao dịch thanh toán không được để trống")
    private String transactionId;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    private BigDecimal amount;

    @NotNull(message = "InvoiceId không được để trống")
    private Integer invoiceId;
}
