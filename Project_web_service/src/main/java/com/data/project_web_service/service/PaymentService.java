package com.data.project_web_service.service;

import com.data.project_web_service.model.dto.request.PaymentDto;
import com.data.project_web_service.model.entity.Payment;

public interface PaymentService {
    Payment createPayment(PaymentDto dto);
    Payment getPaymentDetail(Integer id);
}
