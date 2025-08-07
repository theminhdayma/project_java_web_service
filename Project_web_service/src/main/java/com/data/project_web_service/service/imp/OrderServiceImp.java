package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.OrderDto;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.dto.response.Pagination;
import com.data.project_web_service.model.entity.*;
import com.data.project_web_service.repository.*;
import com.data.project_web_service.service.OrderService;
import com.data.project_web_service.service.CartItemService;
import com.data.project_web_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public PagedResponse<Order> getOrders(Pageable pageable, String username, boolean isAdminOrSales) {
        Page<Order> orderPage;
        if (isAdminOrSales) {
            orderPage = orderRepository.findAll(pageable);
        } else {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new RuntimeException("Không tìm thấy người dùng");
            }
            orderPage = orderRepository.findByUser(user, pageable);
        }

        Pagination pagination = new Pagination(
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalPages(),
                orderPage.getTotalElements()
        );

        return new PagedResponse<>(orderPage.getContent(), pagination);
    }

    @Override
    public Order getOrderDetail(Integer orderId, String username, boolean isAdminOrSales) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));

        if (!isAdminOrSales) {
            if (!order.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Bạn không có đơn hàng này");
            }
        }
        return order;
    }

    @Override
    public Order createOrder(OrderDto orderDto, String username) {
        // Lấy user
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        // Lấy giỏ hàng
        List<CartItem> cartItems = cartItemService.findAllByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của bạn đang trống");
        }

        // Kiểm tra tồn kho
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ tồn kho");
            }
        }

        // Tính tổng tiền
        BigDecimal totalOrderPrice = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo order mới
        Order order = Order.builder()
                .user(user)
                .shippingAddress(orderDto.getShippingAddress())
                .internalNote(orderDto.getInternalNote())
                .totalPrice(totalOrderPrice)
                .status(Order.OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        // Tạo order items, trừ tồn kho sản phẩm
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // Trừ stock
            int newStock = product.getStock() - cartItem.getQuantity();
            product.setStock(newStock);
            // Cập nhật thời gian cập nhật nếu cần
            product.setUpdatedAt(LocalDate.now());
            productRepository.save(product);

            // Tạo OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // Xóa giỏ hàng sau khi tạo order
        cartItemService.deleteAllByUserId(user.getId());

        return order;
    }

    @Override
    public void updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));

        order.setStatus(status);
        order.setUpdatedAt(LocalDate.now());
        orderRepository.save(order);
    }

    @Override
    public void deleteCartForUser(Integer userId) {
        cartItemService.deleteAllByUserId(userId);
    }

    @Override
    public Order updateOrderInfo(Integer orderId, OrderDto orderDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể cập nhật đơn hàng khi trạng thái là PENDING");
        }
        order.setShippingAddress(orderDto.getShippingAddress());
        order.setInternalNote(orderDto.getInternalNote());
        order.setUpdatedAt(LocalDate.now());
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Integer orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDate.now());
        orderRepository.save(order);

        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
            }
        }

        if (reason != null && !reason.isBlank()) {
            order.setInternalNote(
                    (order.getInternalNote() != null ? order.getInternalNote() + "\n" : "") +
                            "[CANCELLED] " + reason
            );
            orderRepository.save(order);
        }
    }
}
