package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.service.DashboardService;
import com.cydeo.service.impl.DashboardServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardServiceImpl dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String getTotalPurchasedInvoices(Model model) {

        model.addAttribute("summaryNumbers", dashboardService.getTotalCostAndSalesAndProfit_loss());
        model.addAttribute("invoices", dashboardService.getLast3ApprovedInvoices() );

        return "/dashboard";
    }

}
