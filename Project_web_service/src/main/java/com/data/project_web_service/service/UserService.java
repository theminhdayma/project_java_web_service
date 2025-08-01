package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.*;
import com.data.project_web_service.model.dto.response.JWTResponse;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.dto.response.ProfileResponse;
import com.data.project_web_service.model.entity.User;

public interface UserService {
    User registerUser(UserRegister ur);
    JWTResponse loginUser(UserLogin ul);
    User verifyPassword(String username, String password);
    String generateOtp();
    JWTResponse verifyOtpAndLogin(OtpRequest request);
    void logoutAllSessions();
    void save(User user);
    ProfileResponse getProfileByUsername(String username);
    ProfileResponse updateProfile(String username, ProfileResponse updateRequest);
    void requestChangePasswordOtp(ChangePasswordRequest request);
    void verifyOtpAndChangePassword(ChangePasswordVerifyRequest request);
    PagedResponse<ProfileResponse> getAllUsers(int page, int size);
    ProfileResponse getUserById(Integer id);
    void deleteUser(Integer id);
}
