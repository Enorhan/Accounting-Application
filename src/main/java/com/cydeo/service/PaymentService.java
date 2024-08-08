package com.cydeo.service;

import com.cydeo.dto.PaymentDto;

import java.util.List;

public interface PaymentService {

    List<PaymentDto> getPaymentForYear(int year);
    void createPaymentsForYear(int year);
    void processPayment(Long paymentId);
    PaymentDto getInvoice(Long id);

}
