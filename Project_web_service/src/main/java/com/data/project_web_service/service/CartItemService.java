package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.CartItemDto;
import com.data.project_web_service.model.entity.CartItem;

import java.util.List;

public interface CartItemService {
    List<CartItem> findAllByUserId(Integer userId);

    CartItem addOrUpdateCartItem(Integer userId, CartItemDto dto);

    CartItem updateCartItem(Integer id, Integer userId, Integer quantity);

    void deleteCartItem(Integer id, Integer userId);

    void deleteAllByUserId(Integer userId);
}
