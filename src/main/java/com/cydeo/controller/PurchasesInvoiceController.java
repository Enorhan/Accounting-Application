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
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {

    private final InvoiceService invoiceService;

    public PurchasesInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/list")
    public String listPurchaseInvoices(Model model){
        List<InvoiceDto> invoices = invoiceService.listAllInvoicesByType(InvoiceType.PURCHASE);

        model.addAttribute("invoices", invoices);

        return "invoice/purchase-invoice-list";

    }
}
