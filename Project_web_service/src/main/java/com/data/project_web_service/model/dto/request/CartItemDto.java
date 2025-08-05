package com.data.project_web_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Integer id;

    @NotBlank(message = "Số lượng sản phẩm không được để trống")
    private Integer quantity;

    private Integer userId;
    private Integer productId;
}
