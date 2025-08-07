package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.*;
import com.data.project_web_service.model.dto.response.JWTResponse;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.dto.response.Pagination;
import com.data.project_web_service.model.dto.response.ProfileResponse;
import com.data.project_web_service.model.entity.Role;
import com.data.project_web_service.model.entity.User;
import com.data.project_web_service.repository.RoleRepository;
import com.data.project_web_service.repository.UserRepository;
import com.data.project_web_service.sercurity.jwt.JWTProvider;
import com.data.project_web_service.sercurity.principal.CustomUserDetails;
import com.data.project_web_service.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public User registerUser(UserRegister ur) {
        if(userRepository.existsByUsername(ur.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if(userRepository.existsByEmail(ur.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if(ur.getRoles() != null && !ur.getRoles().isEmpty()) {
            boolean hasOnlyCustomer = ur.getRoles().stream()
                    .allMatch(r -> r.equalsIgnoreCase("CUSTOMER") || r.equalsIgnoreCase("ROLE_CUSTOMER"));
            if(!hasOnlyCustomer) {
                throw new RuntimeException("Chỉ có thể đăng ký với vai trò CUSTOMER");
            }
        }

        List<Role> assignedRoles = new ArrayList<>();
        assignedRoles.add(roleRepository.findRoleByRoleName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại")));

        User user = User.builder()
                .username(ur.getUsername())
                .password(passwordEncoder.encode(ur.getPassword()))
                .email(ur.getEmail())
                .fullName(ur.getFullName())
                .address(ur.getAddress())
                .phone(ur.getPhone())
                .roles(assignedRoles)
                .status(true)
                .isVerify(false)
                .build();

        String otp = generateOtp();
        user.setOtp(otp);

        User savedUser = userRepository.save(user);

        sendOtpEmail(savedUser, otp);

        return savedUser;
    }

    private void sendOtpEmail(User user, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("theminh2005z@gmail.com");
            helper.setSubject("Mã OTP xác thực đăng ký");

            String text = "Chào " + user.getFullName() + ",\n\n"
                    + "Mã OTP của bạn là: " + otp + "\n\n"
                    + "Xin cảm ơn!";

            helper.setText(text, false);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JWTResponse loginUser(UserLogin ul) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(ul.getUsername(), ul.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            if (!userDetails.getStatus()) {
                throw new RuntimeException("Tài khoản của bạn đã bị khóa.");
            }

            if (!userDetails.getIsVerify()) {
                throw new RuntimeException("Tài khoản chưa được xác thực. Vui lòng xác thực email.");
            }

            String token = jwtProvider.generateToken(userDetails.getUsername());

            return JWTResponse.builder()
                    .username(userDetails.getUsername())
                    .token(token)
                    .authorities(userDetails.getAuthorities())
                    .email(userDetails.getEmail())
                    .status(userDetails.getStatus())
                    .build();

        } catch (AuthenticationException e) {
            log.error("Sai username hoặc password: {}", e.getMessage());
            throw new RuntimeException("Username hoặc password không chính xác");
        }
    }

    @Override
    public void logout(String token) {
    }


    @Override
    public User verifyPassword(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        return user;
    }

    @Override
    public String generateOtp() {
        int otp = (int) (100000 + Math.random() * 900000);
        return String.valueOf(otp);
    }

    @Override
    public JWTResponse verifyOtpAndLogin(OtpRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        if (!request.getOtp().equals(user.getOtp())) {
            throw new RuntimeException("OTP không chính xác");
        }

        user.setOtp(null);
        user.setIsVerify(true);
        userRepository.save(user);

        String token = jwtProvider.generateToken(user.getUsername());

        return JWTResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .token(token)
                .build();
    }

    @Override
    public void logoutAllSessions() {

    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public ProfileResponse getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("Không tìm thấy người dùng!");
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .build();
    }

    @Override
    public ProfileResponse updateProfile(String username, ProfileResponse updateRequest) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        if (updateRequest.getFullName() != null)
            user.setFullName(updateRequest.getFullName());
        if (updateRequest.getPhone() != null)
            user.setPhone(updateRequest.getPhone());
        if (updateRequest.getAddress() != null)
            user.setAddress(updateRequest.getAddress());
        user.setUpdatedAt(LocalDate.now());

        User updated = userRepository.save(user);
        return ProfileResponse.builder()
                .id(updated.getId())
                .username(updated.getUsername())
                .email(updated.getEmail())
                .fullName(updated.getFullName())
                .phone(updated.getPhone())
                .address(updated.getAddress())
                .avatar(updated.getAvatar())
                .status(updated.getStatus())
                .build();
    }

    @Override
    public void requestChangePasswordOtp(ChangePasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng với username này");
        }
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email không khớp với người dùng");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        String otp = generateOtp();

        user.setOtp(otp);
        userRepository.save(user);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("theminh2005z@gmail.com");
            helper.setSubject("Mã OTP xác thực đổi mật khẩu");

            String text = "Chào " + user.getFullName() + ",\n\n"
                    + "Mã OTP đổi mật khẩu của bạn là: " + otp + "\n\n"
                    + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\nXin cảm ơn!";

            helper.setText(text, false);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Gửi email OTP đổi mật khẩu lỗi: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi gửi email OTP");
        }
    }

    @Override
    public void verifyOtpAndChangePassword(ChangePasswordVerifyRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("OTP không chính xác hoặc đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setOtp(null);

        user.setUpdatedAt(LocalDate.now());

        userRepository.save(user);
    }

    @Override
    public PagedResponse<ProfileResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        List<ProfileResponse> profiles = userPage.getContent().stream()
                .map(user -> ProfileResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .address(user.getAddress())
                        .avatar(user.getAvatar())
                        .status(user.getStatus())
                        .build())
                .toList();

        Pagination pagination = new Pagination(
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalPages(),
                userPage.getTotalElements()
        );

        return new PagedResponse<>(profiles, pagination);
    }

    @Override
    public ProfileResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));

        if (user.getRoles().stream().anyMatch(role -> role.getRoleName() == Role.RoleName.ADMIN)) {
            throw new RuntimeException("Không thể xóa người dùng có quyền ADMIN");
        }

        user.setIsDeleted(true);
        user.setDeletedAt(LocalDate.now());
        userRepository.save(user);
    }

    @Override
    public void updateUserStatus(Integer id, Boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));

        if (user.getRoles().stream().anyMatch(role -> role.getRoleName() == Role.RoleName.ADMIN)) {
            throw new RuntimeException("Không thể thay đổi trạng thái người dùng có quyền ADMIN");
        }

        user.setStatus(status);
        user.setUpdatedAt(LocalDate.now());
        userRepository.save(user);
    }
}
