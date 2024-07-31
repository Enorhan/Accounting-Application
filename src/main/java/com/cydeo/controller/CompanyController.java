package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String createCompany(Model model) {
            model.addAttribute("newCompany", new CompanyDto());
            model.addAttribute("companies", companyService.getAllCompanies());
            return "company/company-create";
    }

    @PostMapping("/create")
    public String saveCompany(@Valid @ModelAttribute("newCompany") CompanyDto companyDto, BindingResult bindingResult , Model model) {
        if(bindingResult.hasErrors()) {
          //  model.addAttribute("newCompany", companyDto);
            model.addAttribute("companies", companyService.getAllCompanies());
            return "company/company-create";
        }
        if (companyService.existsByTitle(companyDto)) {
            bindingResult.rejectValue("title","",
                    "A company with this title already exists. Please try with different title.");
        }
        companyService.save(companyDto);

        return "redirect:/companies/list";
    }
    @GetMapping("/update/{id}")
    public String getEditCompany(@PathVariable Long id, Model model) {
        CompanyDto companyDto = companyService.findById(id);
        model.addAttribute("company", companyDto);
        return "company/company-update";
    }

    @PostMapping("/update/{id}")
    public String updateCompany(@Valid @ModelAttribute("company") CompanyDto companyDto, BindingResult bindingResult, Model model) {
        if (companyService.existsByTitle(companyDto)) {
            bindingResult.rejectValue("title","",
                    "A company with this title already exists. Please try with different title.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", companyDto);
            return "company/company-update";

        }

        companyService.updateCompany(companyDto);
        return "redirect:/companies/list";
    }

   @GetMapping("/activate/{id}")
   public String activateCompany(@PathVariable Long id , Model model) {
       model.addAttribute("company",companyService.activateCompany(id));

       return "redirect:/companies/list";
   }

   @GetMapping("/deactivate/{id}")
   public String deactivateCompany(@PathVariable Long id , Model model) {
       model.addAttribute("company",companyService.deactivateCompany(id));

       return "redirect:/companies/list";
   }
}
