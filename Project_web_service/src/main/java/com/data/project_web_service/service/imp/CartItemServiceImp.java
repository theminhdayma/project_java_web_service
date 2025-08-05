package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.CartItemDto;
import com.data.project_web_service.model.entity.CartItem;
import com.data.project_web_service.model.entity.Product;
import com.data.project_web_service.model.entity.User;
import com.data.project_web_service.repository.CartItemRepository;
import com.data.project_web_service.repository.ProductRepository;
import com.data.project_web_service.repository.UserRepository;
import com.data.project_web_service.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CartItemServiceImp implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<CartItem> findAllByUserId(Integer userId) {
        return cartItemRepository.findByUser_IdAndQuantityGreaterThan(userId, 0);
    }

    @Override
    public CartItem addOrUpdateCartItem(Integer userId, CartItemDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        CartItem existing = cartItemRepository.findByUser_IdAndProduct_Id(userId, product.getId());

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            existing.setUpdatedAt(LocalDate.now());
            return cartItemRepository.save(existing);
        } else {
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .build();
            return cartItemRepository.save(newItem);
        }
    }

    @Override
    public CartItem updateCartItem(Integer id, Integer userId, Integer quantity) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item không tồn tại"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không thể cập nhật item của người dùng khác");
        }

        if (quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        item.setQuantity(quantity);
        item.setUpdatedAt(LocalDate.now());
        return cartItemRepository.save(item);
    }

    @Override
    public void deleteCartItem(Integer id, Integer userId) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item không tồn tại"));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không thể xóa item của người dùng khác");
        }

        cartItemRepository.delete(item);
    }

    @Override
    public void deleteAllByUserId(Integer userId) {
        List<CartItem> items = cartItemRepository.findByUser_Id(userId);
        if (items != null && !items.isEmpty()) {
            cartItemRepository.deleteAll(items);
        }
    }
}