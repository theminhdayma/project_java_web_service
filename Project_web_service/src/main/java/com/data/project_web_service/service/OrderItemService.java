package com.data.project_web_service.service;

import com.data.project_web_service.model.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

}
