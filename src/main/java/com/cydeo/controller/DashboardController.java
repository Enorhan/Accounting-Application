package com.cydeo.controller;

import com.cydeo.service.DashboardService;
import com.cydeo.service.impl.CurrencyServiceImpl;
import com.cydeo.service.impl.DashboardServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrencyServiceImpl currencyService;

    public DashboardController(DashboardServiceImpl dashboardService, CurrencyServiceImpl currencyService) {
        this.dashboardService = dashboardService;
        this.currencyService = currencyService;
    }

    @GetMapping
    public String getTotalPurchasedInvoices(Model model) {

        model.addAttribute("summaryNumbers", dashboardService.getTotalCostAndSalesAndProfit_loss());
        model.addAttribute("invoices", dashboardService.getLast3ApprovedInvoices() );
        model.addAttribute("exchangeRates", currencyService.getDataFromApi());

        return "/dashboard";
    }

}
