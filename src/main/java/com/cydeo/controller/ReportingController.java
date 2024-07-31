package com.cydeo.controller;


import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ReportingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportingController {

    private final ReportingService reportingService;
    private final InvoiceProductService invoiceProductService;

    public ReportingController(ReportingService reportingService, InvoiceProductService invoiceProductService, InvoiceProductService invoiceProductService1) {
        this.reportingService = reportingService;
        this.invoiceProductService = invoiceProductService1;
    }

    @GetMapping("/stockData")
    public String stockReport(Model model){

        List<InvoiceProductDto> invoiceProducts = reportingService.getStockData();
        model.addAttribute("invoiceProducts", invoiceProducts);

        return "/report/stock-report";
    }

    @GetMapping("/profitLossData")
    public String profitLoss(Model model){

        Map<String, BigDecimal> monthlyProfitLossDataMap = reportingService.getMonthlyProfitLossData();
        model.addAttribute("monthlyProfitLossDataMap", monthlyProfitLossDataMap);


        return "/report/profit-loss-report";
    }


}
