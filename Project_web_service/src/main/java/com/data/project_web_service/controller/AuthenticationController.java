package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.request.*;
import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.model.dto.response.JWTResponse;
import com.data.project_web_service.model.dto.response.ProfileResponse;
import com.data.project_web_service.model.entity.User;
import com.data.project_web_service.sercurity.principal.CustomUserDetails;
import com.data.project_web_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<User>> register(@Valid @RequestBody UserRegister userRegister) {
        User user = userService.registerUser(userRegister);
        APIResponse<User> response = new APIResponse<>(
                true,
                "Đăng ký thành công. Vui lòng kiểm tra email để nhận mã OTP xác thực.",
                user,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<APIResponse<JWTResponse>> verifyOtp(@Valid @RequestBody OtpRequest request) {
        JWTResponse jwtResponse = userService.verifyOtpAndLogin(request);
        APIResponse<JWTResponse> response = new APIResponse<>(
                true,
                "Xác thực OTP thành công",
                jwtResponse,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<JWTResponse>> login(@Valid @RequestBody UserLogin userLogin) {
        JWTResponse jwtResponse = userService.loginUser(userLogin);
        APIResponse<JWTResponse> response = new APIResponse<>(
                true,
                "Đăng nhập thành công",
                jwtResponse,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse<ProfileResponse>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            APIResponse<ProfileResponse> errorResponse = new APIResponse<>(
                    false,
                    "Bạn chưa đăng nhập hoặc token không hợp lệ",
                    null,
                    HttpStatus.UNAUTHORIZED,
                    null,
                    LocalDateTime.now()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        ProfileResponse profile = userDetails.getUsername() != null
                ? userService.getProfileByUsername(userDetails.getUsername())
                : null;

        APIResponse<ProfileResponse> response = new APIResponse<>(
                true,
                "Lấy thông tin tài khoản thành công",
                profile,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<APIResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileResponse profileUpdateReq) {

        if (userDetails == null) {
            APIResponse<ProfileResponse> errorResponse = new APIResponse<>(
                    false,
                    "Bạn chưa đăng nhập hoặc token không hợp lệ",
                    null,
                    HttpStatus.UNAUTHORIZED,
                    null,
                    LocalDateTime.now()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        ProfileResponse updatedProfile = userService.updateProfile(userDetails.getUsername(), profileUpdateReq);

        APIResponse<ProfileResponse> response = new APIResponse<>(
                true,
                "Cập nhật thông tin thành công",
                updatedProfile,
                HttpStatus.OK,
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<APIResponse<String>> requestChangePasswordOtp(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.requestChangePasswordOtp(request);
            return ResponseEntity.ok(new APIResponse<>(
                    true,
                    "Đã gửi mã OTP đến email của bạn. Vui lòng kiểm tra email để tiếp tục.",
                    null,
                    HttpStatus.OK,
                    null,
                    LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    HttpStatus.BAD_REQUEST,
                    null,
                    LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/change-password/otp")
    public ResponseEntity<APIResponse<String>> verifyOtpAndChangePassword(@Valid @RequestBody ChangePasswordVerifyRequest request) {
        try {
            userService.verifyOtpAndChangePassword(request);
            return ResponseEntity.ok(new APIResponse<>(
                    true,
                    "Đổi mật khẩu thành công",
                    null,
                    HttpStatus.OK,
                    null,
                    LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    HttpStatus.BAD_REQUEST,
                    null,
                    LocalDateTime.now()
            ));
        }
    }


}
