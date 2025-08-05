package com.data.project_web_service.repository;

import com.data.project_web_service.model.entity.Order;
import com.data.project_web_service.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);
}
