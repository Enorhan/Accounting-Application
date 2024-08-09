package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.enums.InvoiceType;
import com.cydeo.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {
    private final InvoiceService invoiceService;
    private final ClientVendorService clientVendorService;
    private final InvoiceProductService invoiceProductService;
    private final ProductService productService;
    private final CompanyService companyService;

    public SalesInvoiceController(InvoiceService invoiceService, ClientVendorService clientVendorService, InvoiceProductService invoiceProductService, ProductService productService, CompanyService companyService) {
        this.invoiceService = invoiceService;
        this.clientVendorService = clientVendorService;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String listSaleInvoices(Model model) {
        List<InvoiceDto> invoices = invoiceService.listAllInvoicesByType(InvoiceType.SALES);

        model.addAttribute("invoices", invoices);

        return "invoice/sales-invoice-list";

    }

    @GetMapping("/create")
    public String createSalesInvoice(Model model) {

        InvoiceDto newInvoiceDto = new InvoiceDto();
        newInvoiceDto.setInvoiceNo(invoiceService.createNewSalesInvoiceNo());
        newInvoiceDto.setDate(LocalDate.now());

        model.addAttribute("newSalesInvoice", newInvoiceDto);
        model.addAttribute("clients", clientVendorService.findAllByCurrentCompanyClientVendorTypeAndIsDeleted(ClientVendorType.CLIENT, false));

        return "invoice/sales-invoice-create";
    }


    @PostMapping("/create")
    public String saveCreatedSalesInvoice(@Valid @ModelAttribute("newSalesInvoice") InvoiceDto invoiceDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("newSalesInvoice", invoiceDto);
            model.addAttribute("clients", clientVendorService.findAllByCurrentCompanyClientVendorTypeAndIsDeleted(ClientVendorType.CLIENT, false));

            return "invoice/sales-invoice-create";
        }

        invoiceService.save(invoiceDto, InvoiceType.SALES);

        InvoiceDto savedInvoice = invoiceService.findByInvoiceNo(invoiceDto.getInvoiceNo());

        return "redirect:/salesInvoices/update/" + savedInvoice.getId();
    }


    @GetMapping("/update/{invoiceId}")
    public String editSalesInvoice(@PathVariable("invoiceId") Long invoiceId, Model model) {

        model.addAttribute("invoice", invoiceService.findById(invoiceId));
        model.addAttribute("clients", clientVendorService.findAllByCurrentCompanyClientVendorTypeAndIsDeleted(ClientVendorType.CLIENT, false));
        model.addAttribute("products", productService.findAllInStock());
        model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false));
        model.addAttribute("newInvoiceProduct", new InvoiceProductDto());


        return "invoice/sales-invoice-update";
    }


    @GetMapping("/removeInvoiceProduct/{invoiceId}/{invoiceProductId}")
    public String removeInvoiceProduct(@PathVariable("invoiceId") Long invoiceId,
                                       @PathVariable("invoiceProductId") Long invoiceProductId) {
        invoiceProductService.removeInvoiceProduct(invoiceProductId);
        return "redirect:/salesInvoices/update/" + invoiceId;
    }


    @PostMapping("/addInvoiceProduct/{invoiceId}")
    public String addInvoiceProduct(
            @PathVariable("invoiceId") Long invoiceId,
            @Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (invoiceService.isQuantityAvailable(invoiceProductDto)) {
            bindingResult.rejectValue(
                    "quantity", "error.newInvoiceProduct", "Not enough "
                            + invoiceProductDto.getProduct().getName() + " quantity to sell."
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("clients", clientVendorService.findAllByCurrentCompanyClientVendorTypeAndIsDeleted(ClientVendorType.CLIENT, false));
            model.addAttribute("products", productService.findAllInStock());
            model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false));

            return "invoice/sales-invoice-update";
        }

        invoiceProductService.save(invoiceProductDto, invoiceId);
        return "redirect:/salesInvoices/update/" + invoiceId;
    }

    @GetMapping("/delete/{invoiceId}")
    public String deleteSaleInvoices(@PathVariable("invoiceId") String invoiceId) {

        invoiceService.delete(Long.valueOf(invoiceId));

        return "redirect:/salesInvoices/list";

    }

    @GetMapping("/print/{invoiceId}")
    public String printInvoice(@PathVariable("invoiceId") Long invoiceId, Model model) {
        CompanyDto companyDto = companyService.getCompanyDtoByLoggedInUser();
        InvoiceDto invoiceDto = invoiceService.findById(invoiceId);
        List<InvoiceProductDto> invoiceProductDtos = invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false);

        model.addAttribute("company", companyDto);
        model.addAttribute("invoice", invoiceDto);
        model.addAttribute("invoiceProducts", invoiceProductDtos);

        return "invoice/invoice_print";
    }


    @PostMapping("/update/{invoiceId}")
    public String insertSalesInvoice(@PathVariable("invoiceId") Long invoiceId,
                                     @Valid @ModelAttribute("invoice") InvoiceDto invoiceDto,
                                     BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("clients",
                    clientVendorService.findAllByCurrentCompanyClientVendorTypeAndIsDeleted(ClientVendorType.CLIENT, false));
            model.addAttribute("products", productService.findAllInStock());
            model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false));
            return "invoice/sales-invoice-update";
        }

        invoiceService.update(invoiceDto, invoiceId);

        return "redirect:/salesInvoices/update/" + invoiceId;
    }


    @GetMapping("/approve/{invoiceId}")
    public String approveInvoice(@PathVariable("invoiceId") Long invoiceId) {

        invoiceService.approveSalesInvoice(invoiceId);

        return "redirect:/salesInvoices/list";
    }


}

