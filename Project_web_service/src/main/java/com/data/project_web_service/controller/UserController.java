package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.dto.response.ProfileResponse;
import com.data.project_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<APIResponse<PagedResponse<ProfileResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        PagedResponse<ProfileResponse> users = userService.getAllUsers(page, size);

        APIResponse<PagedResponse<ProfileResponse>> response = new APIResponse<>(
                true,
                "Lấy danh sách người dùng thành công",
                users,
                null,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ProfileResponse>> getUserById(@PathVariable Integer id) {
        ProfileResponse user = userService.getUserById(id);
        APIResponse<ProfileResponse> response = new APIResponse<>(
                true,
                "Lấy thông tin người dùng thành công",
                user,
                null,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<ProfileResponse>> updateUser(
            @PathVariable Integer id,
            @RequestBody ProfileResponse updateRequest) {

        ProfileResponse user = userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ProfileResponse updatedUser = userService.updateProfile(user.getUsername(), updateRequest);
        APIResponse<ProfileResponse> response = new APIResponse<>(
                true,
                "Cập nhật thông tin người dùng thành công",
                updatedUser,
                null,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        APIResponse<String> response = new APIResponse<>(
                true,
                "Xóa người dùng thành công",
                null,
                null,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
}
