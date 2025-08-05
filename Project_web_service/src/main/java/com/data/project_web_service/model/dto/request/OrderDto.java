package com.data.project_web_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Integer id;

    @NotNull(message = "User ID không được để trống")
    private Integer userId;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;

    private String internalNote;

    @NotNull(message = "Tổng tiền đơn hàng không được để trống")
    @Positive(message = "Tổng tiền đơn hàng phải lớn hơn 0")
    private BigDecimal totalPrice;
}
