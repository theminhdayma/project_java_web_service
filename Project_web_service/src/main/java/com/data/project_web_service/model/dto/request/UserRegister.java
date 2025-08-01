package com.data.project_web_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegister {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$",
            message = "Password phải có độ dài từ 8 đến 16 ký tự, bao gồm ít nhất một chữ cái, một số và một ký tự đặc biệt."
    )
    private String password;

    @NotBlank(message = "Email không được để trống")
    private String email;

    private Boolean status;

    @NotBlank(message = "Full name không được để trống")
    private String fullName;

    @NotBlank(message = "Address không được để trống")
    private String address;

    private String phone;

    private Boolean isVerify;

    private List<String> roles;
}
