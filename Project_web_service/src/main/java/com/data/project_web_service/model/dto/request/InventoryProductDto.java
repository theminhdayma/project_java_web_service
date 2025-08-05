package com.data.project_web_service.model.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryProductDto {
    private Integer productId;
    private String name;
    private Integer stock;
}
