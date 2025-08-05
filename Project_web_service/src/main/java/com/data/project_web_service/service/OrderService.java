package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.OrderDto;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.entity.Order;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    PagedResponse<Order> getOrders(Pageable pageable, String username, boolean isAdminOrSales);

    Order getOrderDetail(Integer orderId, String username, boolean isAdminOrSales);

    Order createOrder(OrderDto orderDto, String username);

    void updateOrderStatus(Integer orderId, Order.OrderStatus status);

    void deleteCartForUser(Integer userId);

    Order updateOrderInfo(Integer orderId, OrderDto orderDto);

    void cancelOrder(Integer orderId, String reason);

}
