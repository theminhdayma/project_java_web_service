package com.data.project_web_service.controller;

import com.data.project_web_service.model.entity.CartItem;
import com.data.project_web_service.repository.UserRepository;
import com.data.project_web_service.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.data.project_web_service.model.dto.request.*;
import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.sercurity.principal.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart-items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<APIResponse<List<CartItem>>> getCartItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        Integer userId = userRepository.findByUsername(userDetails.getUsername()).getId();
        List<CartItem> items = cartItemService.findAllByUserId(userId);

        return ResponseEntity.ok(new APIResponse<>(true, "Lấy danh sách giỏ hàng thành công", items, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @PostMapping
    public ResponseEntity<APIResponse<CartItem>> addCartItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @Valid @RequestBody CartItemDto dto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        Integer userId = userRepository.findByUsername(userDetails.getUsername()).getId();

        CartItem item = cartItemService.addOrUpdateCartItem(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Thêm sản phẩm vào giỏ hàng thành công", item, HttpStatus.CREATED, null, LocalDateTime.now()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<CartItem>> updateCartItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @PathVariable Integer id,
                                                                @Valid @RequestBody CartItemDto dto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        Integer userId = userRepository.findByUsername(userDetails.getUsername()).getId();

        CartItem item = cartItemService.updateCartItem(id, userId, dto.getQuantity());
        return ResponseEntity.ok(new APIResponse<>(true, "Cập nhật giỏ hàng thành công", item, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteCartItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Integer id) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        Integer userId = userRepository.findByUsername(userDetails.getUsername()).getId();

        cartItemService.deleteCartItem(id, userId);
        return ResponseEntity.ok(new APIResponse<>(true, "Xóa sản phẩm khỏi giỏ hàng thành công", null, HttpStatus.OK, null, LocalDateTime.now()));
    }
}
