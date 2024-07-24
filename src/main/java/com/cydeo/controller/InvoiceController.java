package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/purchaseInvoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/list")
    public String createInvoice(Model model){
        List<InvoiceDto> invoices = invoiceService.listAllInvoices();

        model.addAttribute("invoices", invoices);

        return "invoice/purchase-invoice-list";

    }
}
