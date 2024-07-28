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
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final ClientVendorService clientVendorService;
    private final InvoiceProductService invoiceProductService;
    private final ProductService productService;

    public SalesInvoiceController(InvoiceService invoiceService, ClientVendorService clientVendorService, InvoiceProductService invoiceProductService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.clientVendorService = clientVendorService;
        this.invoiceProductService = invoiceProductService;
        this.productService = productService;
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


    @GetMapping("/update/{invoiceId}")
    public String editSalesInvoice(@PathVariable("invoiceId") Long invoiceId,Model model){

        //Long invoiceCompanyId=invoiceService.findById(invoiceId).getCompany().getId();

        model.addAttribute("invoice",invoiceService.findById(invoiceId));
        model.addAttribute("clients",clientVendorService.findAll());
        model.addAttribute("products",productService.findAllInStock());
        model.addAttribute("invoiceProducts",invoiceProductService.findAllByInvoiceId(invoiceId));
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
    public String addInvoiceProduct(@PathVariable("invoiceId") Long invoiceId,
                                    @Valid @ModelAttribute("newInvoiceProduct") InvoiceProductDto invoiceProductDto,
                                    BindingResult bindingResult,
                                    Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("invoice", invoiceService.findById(invoiceId));
            model.addAttribute("clients", clientVendorService.findAll());
            model.addAttribute("products", productService.findAllInStock());
            model.addAttribute("invoiceProducts", invoiceProductService.findAllByInvoiceId(invoiceId));
            return "invoice/sales-invoice-update";
        }


        invoiceProductService.save(invoiceProductDto,invoiceId);

        return "redirect:/salesInvoices/update/" + invoiceId;
    }


    @GetMapping("/delete/{invoiceId}")
    public String deleteSaleInvoices(@PathVariable("invoiceId") String invoiceId){

        invoiceService.delete(Long.valueOf(invoiceId));

        return "redirect:/salesInvoices/list";

    }

}
