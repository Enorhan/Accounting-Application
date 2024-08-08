package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Payment;
import com.cydeo.enums.Month;
import com.cydeo.repository.PaymentRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.PaymentService;
import com.cydeo.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {


    private final PaymentRepository paymentRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MapperUtil mapperUtil, CompanyService companyService) {
        this.paymentRepository = paymentRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
    }

    private static final BigDecimal FIXED_FEE = BigDecimal.valueOf(250);


    @Override
    public List<PaymentDto> getPaymentForYear(int year) {

        List<Payment> payments = paymentRepository.findByYear(year);


        if (payments.isEmpty()) {
            createPaymentsForYear(year);
            payments = paymentRepository.findByYear(year);
        }
        return payments.stream()
                .sorted(Comparator.comparing(Payment::getMonth))
                .map(payment -> mapperUtil.convert(payment, new PaymentDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void createPaymentsForYear(int year) {
        for (Month month : Month.values()){
            if (paymentRepository.findByYearAndAndMonth(year, month) == null){
                Payment payment = new Payment();
                payment.setYear(year);
                payment.setMonth(month);
                payment.setAmount(FIXED_FEE);
                payment.setPaid(false);
                paymentRepository.save(payment);
            }
        }
    }

    @Override
    public void processPayment(Long paymentId, String companyStripeId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id " + paymentId));
        payment.setPaid(true);
        payment.setPaymentDate(LocalDate.now());
        payment.setCompanyStripeId(companyStripeId);
        CompanyDto company=companyService.findById(companyService.getCompanyIdByLoggedInUser());
        payment.setCompany(mapperUtil.convert(company,new Company()));
        paymentRepository.save(payment);
    }

    @Override
    public PaymentDto getInvoice(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        return mapperUtil.convert(payment, new PaymentDto());
    }

}
