package com.data.project_web_service.model.dto.request;

import com.data.project_web_service.model.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private Integer id;
    @NotBlank(message = "Role name không được để trống")
    private Role.RoleName roleName;
}
