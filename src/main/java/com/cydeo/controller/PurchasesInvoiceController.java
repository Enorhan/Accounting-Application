package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {

    private final InvoiceService invoiceService;
    private final ClientVendorService clientVendorService;

    public PurchasesInvoiceController(InvoiceService invoiceService, ClientVendorService clientVendorService) {
        this.invoiceService = invoiceService;
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listPurchaseInvoices(Model model) {
        List<InvoiceDto> invoices = invoiceService.listAllInvoicesByType(InvoiceType.PURCHASE);

        model.addAttribute("invoices", invoices);

        return "invoice/purchase-invoice-list";
    }

    @GetMapping("/create")
    public String getCreatePurchaseInvoice(Model model) {
        InvoiceDto newInvoice = new InvoiceDto();
        newInvoice.setInvoiceNo(invoiceService.getNewPurchaseInvoiceNumberId());
        newInvoice.setDate(LocalDate.now());

        model.addAttribute("newPurchaseInvoice", newInvoice);
        model.addAttribute("vendors", clientVendorService.findAll());

        return "invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String createPurchaseInvoice(@Valid @ModelAttribute("newPurchaseInvoice") InvoiceDto invoiceDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("newPurchaseInvoice", invoiceDto);
            model.addAttribute("vendors", clientVendorService.findAll());

            return "invoice/purchase-invoice-create";
        }

        invoiceService.save(invoiceDto, InvoiceType.PURCHASE);

        return "redirect:list";
    }
}