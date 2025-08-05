package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.InvoiceDto;
import com.data.project_web_service.model.dto.response.PagedResponse;
import com.data.project_web_service.model.entity.Invoice;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    PagedResponse<Invoice> getInvoices(Pageable pageable);

    Invoice getInvoiceDetail(Integer invoiceId);

    Invoice createInvoice(InvoiceDto dto);

    Invoice updateInvoiceStatus(Integer id, Invoice.InvoiceStatus status);

    Invoice getInvoiceByOrderId(Integer orderId);
}
