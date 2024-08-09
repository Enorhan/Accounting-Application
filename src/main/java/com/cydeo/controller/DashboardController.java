package com.cydeo.controller;

import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.impl.CurrencyServiceImpl;
import com.cydeo.service.impl.DashboardServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final CurrencyServiceImpl currencyService;
    private final InvoiceProductService invoiceProductService;

    public DashboardController(CurrencyServiceImpl currencyService, InvoiceProductService invoiceProductService) {
        this.currencyService = currencyService;
        this.invoiceProductService = invoiceProductService;
    }

    @GetMapping
    public String getDashboardSummaryFunctionalities(Model model) {
        model.addAttribute("summaryNumbers", invoiceProductService.getTotalCostAndSalesAndProfit_loss());
        model.addAttribute("invoices", invoiceProductService.getLast3ApprovedInvoices() );
        model.addAttribute("exchangeRates", currencyService.getDataFromApi());

        return "/dashboard";
    }
}
