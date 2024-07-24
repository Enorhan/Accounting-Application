package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/salesInvoices")
public class SaleInvoiceController {

    private final InvoiceService invoiceService;

    public SaleInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/list")
    public String listSaleInvoices(Model model){
        List<InvoiceDto> invoices = invoiceService.listAllInvoicesByType(InvoiceType.SALES);

        model.addAttribute("invoices", invoices);

        return "invoice/purchase-invoice-list";

    }
}
