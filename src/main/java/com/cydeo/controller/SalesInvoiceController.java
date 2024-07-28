package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceService;
import org.springframework.boot.Banner;
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
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final ClientVendorService clientVendorService;

    public SalesInvoiceController(InvoiceService invoiceService, ClientVendorService clientVendorService) {
        this.invoiceService = invoiceService;
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listSaleInvoices(Model model){
        List<InvoiceDto> invoices = invoiceService.listAllInvoicesByType(InvoiceType.SALES);

        model.addAttribute("invoices", invoices);

        return "invoice/sales-invoice-list";

    }

    @GetMapping("/create")
    public String createSalesInvoice(Model model){

        InvoiceDto newInvoiceDto=new InvoiceDto();
        newInvoiceDto.setInvoiceNo(invoiceService.createNewSalesInvoiceNo());
        newInvoiceDto.setDate(LocalDate.now());

        model.addAttribute("newSalesInvoice",newInvoiceDto);
        model.addAttribute("clients",clientVendorService.listAllClientVendors());

        return "invoice/sales-invoice-create";
    }


    @PostMapping("/create")
    public String saveCreatedSalesInvoice(@Valid @ModelAttribute("newSalesInvoice") InvoiceDto invoiceDto, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()){

            model.addAttribute("newSalesInvoice",invoiceDto);
            model.addAttribute("clients",clientVendorService.listAllClientVendors());

            return "invoice/sales-invoice-create";
        }

        invoiceService.save(invoiceDto,InvoiceType.SALES);

        return "redirect:/salesInvoices/list";
    }


}
