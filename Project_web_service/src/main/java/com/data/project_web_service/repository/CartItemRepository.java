package com.data.project_web_service.repository;

import com.data.project_web_service.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUser_IdAndQuantityGreaterThan(Integer userId, int quantity);

    CartItem findByUser_IdAndProduct_Id(Integer userId, Integer productId);

    List<CartItem> findByUser_Id(Integer userId);
}
