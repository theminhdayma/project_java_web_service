package com.data.project_web_service.controller;

import com.data.project_web_service.model.dto.request.OrderDto;
import com.data.project_web_service.model.dto.response.APIResponse;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.entity.Invoice;
import com.data.project_web_service.model.entity.Order;
import com.data.project_web_service.model.entity.OrderItem;
import com.data.project_web_service.service.InvoiceService;
import com.data.project_web_service.service.OrderService;
import com.data.project_web_service.sercurity.principal.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<APIResponse<PagedResponse<Order>>> getOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));

        PagedResponse<Order> orders = orderService.getOrders(PageRequest.of(page, size), userDetails.getUsername(), isAdminOrSales);

        return ResponseEntity.ok(
                new APIResponse<>(true, "Lấy danh sách đơn hàng thành công", orders, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Order>> getOrderById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));

        Order order = orderService.getOrderDetail(id, userDetails.getUsername(), isAdminOrSales);

        return ResponseEntity.ok(
                new APIResponse<>(true, "Lấy chi tiết đơn hàng thành công", order, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @PostMapping
    public ResponseEntity<APIResponse<Order>> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OrderDto orderDto) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        // Bỏ userId trong orderDto, sử dụng username lấy userId thực tế ở service
        orderDto.setUserId(null);

        Order order = orderService.createOrder(orderDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new APIResponse<>(true, "Tạo đơn hàng thành công", order, HttpStatus.CREATED, null, LocalDateTime.now()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<APIResponse<String>> updateOrderStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam Order.OrderStatus status) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));

        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new APIResponse<>(false, "Bạn không có quyền cập nhật trạng thái đơn hàng", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        orderService.updateOrderStatus(id, status);

        return ResponseEntity.ok(new APIResponse<>(true, "Cập nhật trạng thái đơn hàng thành công", null, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Order>> updateOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @Valid @RequestBody OrderDto orderDto
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));

        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new APIResponse<>(false, "Bạn không có quyền cập nhật thông tin đơn hàng", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        Order updatedOrder = orderService.updateOrderInfo(id, orderDto);

        return ResponseEntity.ok(
                new APIResponse<>(true, "Cập nhật thông tin đơn hàng thành công", updatedOrder, HttpStatus.OK, null, LocalDateTime.now()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam(required = false) String reason
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new APIResponse<>(false, "Bạn không có quyền hủy/xóa đơn hàng", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        orderService.cancelOrder(id, reason);

        return ResponseEntity.ok(new APIResponse<>(true, "Đã hủy đơn hàng và khôi phục kho", null, HttpStatus.OK, null, LocalDateTime.now()));
    }


    @GetMapping("/{id}/items")
    public ResponseEntity<APIResponse<List<OrderItem>>> getOrderItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }

        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));

        Order order = orderService.getOrderDetail(id, userDetails.getUsername(), isAdminOrSales);

        // Nếu không phải admin/sales thì chỉ cho xem nếu là chủ đơn hàng
        if (!isAdminOrSales && !order.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new APIResponse<>(false, "Bạn không có quyền xem items của đơn hàng này", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        List<OrderItem> items = order.getOrderItems();
        return ResponseEntity.ok(new APIResponse<>(true, "Lấy danh sách sản phẩm trong đơn hàng thành công", items, HttpStatus.OK, null, LocalDateTime.now()));
    }

    // GET /api/v1/orders/{orderId}/invoice - lấy invoice của một order cụ thể
    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<APIResponse<Invoice>> getInvoiceByOrderId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer orderId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>(false, "Chưa đăng nhập", null, HttpStatus.UNAUTHORIZED, null, LocalDateTime.now()));
        }
        boolean isAdminOrSales = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SALES"));
        if (!isAdminOrSales) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "Bạn không có quyền truy cập hóa đơn theo order", null, HttpStatus.FORBIDDEN, null, LocalDateTime.now()));
        }

        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(new APIResponse<>(true, "Lấy hóa đơn theo order thành công", invoice, HttpStatus.OK, null, LocalDateTime.now()));
    }
}
