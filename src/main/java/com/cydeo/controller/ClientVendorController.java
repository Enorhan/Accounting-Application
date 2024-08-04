package com.cydeo.controller;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;
    private final InvoiceRepository invoiceRepository;
    private final CountryService countryService;


    public ClientVendorController(ClientVendorService clientVendorService, InvoiceRepository invoiceRepository, CountryService countryService) {
        this.clientVendorService = clientVendorService;
        this.invoiceRepository = invoiceRepository;
        this.countryService = countryService;
    }

    @GetMapping("/list")
    public String getClientVendors(Model model) {
        model.addAttribute("clientVendors", clientVendorService.listAllClientVendorsWithInvoiceStatusByCompany());
        return "clientVendor/clientVendor-list";
    }

    @GetMapping("/create")
    public String createClientVendorForm(Model model) {
        model.addAttribute("newClientVendor", new ClientVendorDto());
        model.addAttribute("clientVendorTypes", Arrays.asList(ClientVendorType.values()));
        model.addAttribute("countries",countryService.getAllCountries());
        return "clientVendor/clientVendor-create";
    }

    @PostMapping("/create")
    public String createClientVendor(@Valid @ModelAttribute("newClientVendor") ClientVendorDto clientVendorDto, BindingResult result, Model model) {

        if (clientVendorService.existsByName(clientVendorDto.getClientVendorName())){
            result.rejectValue("clientVendorName", "", "Client vendor already exists");
        }


        if (result.hasErrors()) {

            model.addAttribute("clientVendorTypes", Arrays.asList(ClientVendorType.values()));
            return "clientVendor/clientVendor-create";
        }

        clientVendorService.createClientVendor(clientVendorDto);
        return "redirect:/clientVendors/list";
    }

    @GetMapping("/update/{id}")
    public String updateClientVendorForm(@PathVariable Long id, Model model) {
        ClientVendorDto clientVendorDto = clientVendorService.findById(id);
        model.addAttribute("clientVendor", clientVendorDto);
        model.addAttribute("clientVendorTypes", ClientVendorType.values());
        model.addAttribute("countries",countryService.getAllCountries());
        return "clientVendor/clientVendor-update";
    }

    @PostMapping("/update/{id}")
    public String updateClientVendor(@PathVariable Long id, @ModelAttribute("clientVendor") @Valid ClientVendorDto clientVendorDto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                System.out.println("Error: " + error.getDefaultMessage());
            }
            model.addAttribute("clientVendorTypes", Arrays.asList(ClientVendorType.values()));
            return "clientVendor/clientVendor-update";
        }

        clientVendorService.updateClientVendor(id, clientVendorDto);
        return "redirect:/clientVendors/list";
    }


    @GetMapping("/delete/{id}")
    public String deleteClientVendor(@PathVariable Long id) {
        if (!clientVendorService.isHasInvoices(id)) {
            clientVendorService.deleteClientVendor(id);
        }
        return "redirect:/clientVendors/list";
    }

}