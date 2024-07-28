package com.cydeo.controller;


import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/stockData")
    public String stockReport(Model model){

        List<InvoiceProductDto> invoiceProducts = reportingService.getStockData();
        model.addAttribute("InvoiceProducts", invoiceProducts);

        return "/report/stock-report";
    }
}
