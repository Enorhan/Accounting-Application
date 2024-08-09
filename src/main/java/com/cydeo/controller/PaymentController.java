package com.cydeo.controller;

import com.cydeo.dto.PaymentDto;
import com.cydeo.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
        Stripe.apiKey = stripeSecretKey;
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
        model.addAttribute("stripePublicKey", stripePublishableKey);
        paymentService.processPayment(id,null);
        return "/payment/payment-method";
    }


    @GetMapping("/toInvoice/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        PaymentDto payment = paymentService.getInvoice(id);

        model.addAttribute("payment", payment);
        model.addAttribute("company", payment.getCompany());
        return "payment/payment-invoice-print";
    }

    @PostMapping("/charge/{id}")
    public ModelAndView charge(@PathVariable("id") Long monthId,
                               @RequestParam("stripeToken") String token,
                               @RequestParam("amount") BigDecimal amount,
                               HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("redirect:/payments/charge/" + monthId);

        try {
            Stripe.apiKey = stripeSecretKey;

            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValueExact());
            chargeParams.put("currency", "usd");
            chargeParams.put("description", "Subscription Fee for Month ID: " + monthId);
            chargeParams.put("source", token);

            Charge charge = Charge.create(chargeParams);

            session.setAttribute("success", true);
            session.setAttribute("chargeId", charge.getId());
            session.setAttribute("description", charge.getDescription());

            paymentService.processPayment(monthId,charge.getId());

        } catch (StripeException e) {
            session.setAttribute("success", false);
            session.setAttribute("error", e.getMessage());
        }

        return modelAndView;
    }

    @GetMapping("/charge/{id}")
    public String paymentResult(@PathVariable("id") Long monthId, HttpSession session, Model model) {
        model.addAttribute("success", session.getAttribute("success"));
        model.addAttribute("chargeId", session.getAttribute("chargeId"));
        model.addAttribute("description", session.getAttribute("description"));
        model.addAttribute("error", session.getAttribute("error"));

        session.removeAttribute("success");
        session.removeAttribute("chargeId");
        session.removeAttribute("description");
        session.removeAttribute("error");

        return "payment/payment-result";
    }
}

