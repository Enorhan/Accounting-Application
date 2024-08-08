package com.cydeo.controller;

import com.cydeo.dto.PaymentDto;
import com.cydeo.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/list")
    public String getPaymentsByYear(@RequestParam(required = false, defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year, Model model) {
        List<PaymentDto> payments = paymentService.getPaymentForYear(year);
        model.addAttribute("year", year);
        model.addAttribute("payments", payments);
        return "payment/payment-list";
    }

    @PostMapping("/create/{year}")
    public String createPaymentsForYear(@PathVariable int year) {
        paymentService.createPaymentsForYear(year);
        return "redirect:/payments/list?year=" + year;
    }

    @GetMapping("/newpayment/{id}")
    public String processPayment(@PathVariable Long id, Model model) {
        PaymentDto payment = paymentService.getInvoice(id);
        model.addAttribute("monthId", id);
        model.addAttribute("payment", payment);
        model.addAttribute("currency", "usd"); // Adjust as needed
        paymentService.processPayment(id);
        return "/payment/payment-method";
    }


    @GetMapping("/toInvoice/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        PaymentDto payment = paymentService.getInvoice(id);

        model.addAttribute("payment", payment);
        model.addAttribute("company", payment.getCompany());
        return "payment/payment-invoice-print";
    }
}
