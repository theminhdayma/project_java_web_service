package com.data.project_web_service.service.imp;

import com.data.project_web_service.model.dto.request.PaymentDto;
import com.data.project_web_service.model.entity.Invoice;
import com.data.project_web_service.model.entity.Payment;
import com.data.project_web_service.repository.InvoiceRepository;
import com.data.project_web_service.repository.PaymentRepository;
import com.data.project_web_service.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
public class PaymentServiceImp implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public Payment createPayment(PaymentDto dto) {
        Invoice invoice = invoiceRepository.findById(dto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với id: " + dto.getInvoiceId()));

        BigDecimal amount = invoice.getTotalAmount(); // Lấy trực tiếp từ invoice

        Payment payment = Payment.builder()
                .method(dto.getMethod())
                .status(dto.getStatus() != null ? dto.getStatus() : Payment.PaymentStatus.PENDING)
                .transactionId(dto.getTransactionId())
                .amount(amount)
                .invoice(invoice)
                .build();

        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        return paymentRepository.save(payment);
    }


    @Override
    public Payment getPaymentDetail(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin thanh toán với id: " + id));
    }
}
