package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.InvoiceDto;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.dto.response.Pagination;
import com.data.project_web_service.model.entity.Invoice;
import com.data.project_web_service.model.entity.Order;
import com.data.project_web_service.repository.InvoiceRepository;
import com.data.project_web_service.repository.OrderRepository;
import com.data.project_web_service.service.InvoiceService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class InvoiceServiceImp implements InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public PagedResponse<Invoice> getInvoices(Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findAll(pageable);
        Pagination pagination = new Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
        return new PagedResponse<>(page.getContent(), pagination);
    }

    @Override
    public Invoice getInvoiceDetail(Integer invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với id: " + invoiceId));
    }

    @Override
    public Invoice createInvoice(InvoiceDto dto) {
        if (dto.getOrderId() == null)
            throw new RuntimeException("OrderId là bắt buộc");

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + dto.getOrderId()));

        BigDecimal totalAmount = order.getTotalPrice();

        if (totalAmount == null) {
            throw new RuntimeException("Đơn hàng chưa có tổng tiền hợp lệ");
        }

        Invoice invoice = Invoice.builder()
                .order(order)
                .totalAmount(totalAmount)
                .status(Invoice.InvoiceStatus.UNPAID)
                .build();

        return invoiceRepository.save(invoice);
    }


    @Override
    public Invoice updateInvoiceStatus(Integer id, Invoice.InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với id: " + id));
        invoice.setStatus(status);
        invoice.setUpdatedAt(LocalDate.now());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice getInvoiceByOrderId(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));
        return invoiceRepository.findByOrder(order);
    }
}
