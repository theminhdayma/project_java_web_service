package com.data.project_web_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Integer id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @PositiveOrZero(message = "Giá sản phẩm phải bằng hoặc lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng sản phẩm không được để trống")
    @PositiveOrZero(message = "Số lượng sản phẩm phải bằng hoặc lớn hơn 0")
    private Integer stock;

    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private Integer categoryId;
}
