package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.entity.OrderItem;
import com.data.project_web_service.repository.OrderItemRepository;
import com.data.project_web_service.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImp implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
