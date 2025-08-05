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
            // Kiểm tra đơn hàng của user hiện tại
            if (!order.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Bạn không có quyền xem đơn hàng này");
            }
        }

        // Nếu cần, bạn có thể lấy orderItems kèm theo vì field orderItems là @OneToMany và fetch LAZY
        // Thường gọi order.getOrderItems() khi cần
        return order;
    }

    @Override
    public Order createOrder(OrderDto orderDto, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        // Tạo đơn hàng mới
        Order order = Order.builder()
                .user(user)
                .shippingAddress(orderDto.getShippingAddress())
                .internalNote(orderDto.getInternalNote())
                .totalPrice(orderDto.getTotalPrice())
                .status(Order.OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);

        // Lấy danh sách giỏ hàng của user từ CartItemService
        List<CartItem> cartItems = cartItemService.findAllByUserId(user.getId());

        // Tạo các order item tương ứng
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())   // giá sản phẩm tại thời điểm bán
                    .build();
            orderItemRepository.save(orderItem);
        }

        // Xóa toàn bộ giỏ hàng của user sau khi tạo order
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
        // Set trạng thái
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDate.now());
        orderRepository.save(order);

        // Restore stock
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                // cần save product nếu JPA không cascade tự động
            }
        }

        // Log reason (nếu có thể, lưu vào table khác hoặc order.internalNote)
        if (reason != null && !reason.isBlank()) {
            order.setInternalNote(
                    (order.getInternalNote() != null ? order.getInternalNote() + "\n" : "") +
                            "[CANCELLED] " + reason
            );
            orderRepository.save(order);
        }
    }


}
