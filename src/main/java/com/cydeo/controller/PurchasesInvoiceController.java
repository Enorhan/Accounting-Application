package com.cydeo.controller;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/purchaseInvoices")
public class PurchasesInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProductService invoiceProductService;
    private final ClientVendorService clientVendorService;
    private final ProductService productService;

    public PurchasesInvoiceController(InvoiceService invoiceService, InvoiceProductService invoiceProductService, ClientVendorService clientVendorService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.invoiceProductService = invoiceProductService;
        this.clientVendorService = clientVendorService;
        this.productService = productService;
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
        model.addAttribute("vendors", clientVendorService.listAllClientVendors());

        return "invoice/purchase-invoice-create";
    }

    @PostMapping("/create")
    public String createPurchaseInvoice(@Valid @ModelAttribute("newPurchaseInvoice") InvoiceDto invoiceDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("newPurchaseInvoice", invoiceDto);
            model.addAttribute("vendors", clientVendorService.listAllClientVendors());

            return "invoice/purchase-invoice-create";
        }

        invoiceService.save(invoiceDto, InvoiceType.PURCHASE);

        return "redirect:list";
    }

    @GetMapping("/update/{invoiceId}")
    public String getUpdatePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, Model model) {

        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        model.addAttribute("vendors", clientVendorService.listAllClientVendors());
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());
        model.addAttribute("products", productService.findAllInStock());
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false));

        return "invoice/purchase-invoice-update";
    }

    @PostMapping("/update/{invoiceId}")
    public String updatePurchaseInvoice(@Valid @ModelAttribute("invoice") InvoiceDto invoiceDto, @PathVariable("invoiceId") Long invoiceId, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vendors", clientVendorService.listAllClientVendors());

            return "invoice/purchase-invoice-update";
        }

        invoiceService.update(invoiceDto, invoiceId);

        return "redirect:/purchaseInvoices/update/{invoiceId}";
    }


    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String updatePurchaseInvoice(@PathVariable("invoiceId") Long invoiceId, @Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vendors", clientVendorService.listAllClientVendors());
            model.addAttribute("products", productService.findAllInStock());
            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false));

            return "invoice/purchase-invoice-update";
        }

        invoiceProductService.save(invoiceProductDto, invoiceId);

        return "redirect:/purchaseInvoices/update/{invoiceId}";
    }


    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProuductId}")
    public String deletePurchaseInvoiceProduct(@PathVariable("invoiceProuductId") String invoiceProuductId) {
        invoiceProductService.delete(Long.valueOf(invoiceProuductId));

        return "redirect:/purchaseInvoices/update/{invoiceId}";
    }
}