package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }


    @GetMapping("/list")
    public String getCompanies(Model model) {
        List<CompanyDto> companies = companyService.getAllCompanies();

        model.addAttribute("companies", companies);
        return "company/company-list";
    }

    @GetMapping("/create")
    public String createCompany(@Valid @ModelAttribute("company") CompanyDto companyDto, BindingResult bindingResult , Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("company", new CompanyDto());
            model.addAttribute("company", companyDto);
            model.addAttribute("companies", companyService.getAllCompanies());

            return "company/company-create";
        }
        companyService.save(companyDto);

        return "redirect:/company-list";
    }
}
