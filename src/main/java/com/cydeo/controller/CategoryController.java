package com.cydeo.controller;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import com.cydeo.service.CategoryService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityService securityService;
    private final MapperUtil mapperUtil;

    public CategoryController(CategoryService categoryService, SecurityService securityService, MapperUtil mapperUtil){
        this.categoryService = categoryService;
        this.securityService = securityService;
        this.mapperUtil = mapperUtil;
    }

    @GetMapping("/list")
    public String getCategoryList(Model model) {
        model.addAttribute("categories", categoryService.listAllCategories());
        return "/category/category-list";

    }

    @GetMapping("/create")
    public String getCategoryCreateForm(Model model) {
        model.addAttribute("newCategory", new Category());

        return "/category/category-create";
    }
    @PostMapping("/create")
    public String submitForm(@ModelAttribute("newCategory") Category category, Model model) {
        model.addAttribute("newCategory", new Category());

        UserDto loggedUser = this.securityService.getLoggedInUser();

        CompanyDto userCompany = loggedUser.getCompany();

        category.setCompany(mapperUtil.convert(userCompany, new Company()));

        categoryService.saveCategory(category);

        return "redirect:/categories/list";

    }

    @GetMapping("/update/{id}")
    public String getCategoryUpdateForm(Model model, @PathVariable Long id) {
        try{
            CategoryDto category = categoryService.findById(id);
            model.addAttribute("category", category);
        }
        catch(Exception e){
            return "redirect:/categories/list";
        }
        return "/category/category-update";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(Model model, @PathVariable Long id, @ModelAttribute("newCategory") Category category){
        try{
            CategoryDto updatedCategory = this.categoryService.saveCategory(category);
            model.addAttribute("category", updatedCategory);
        }catch(Exception e){
            model.addAttribute("category", category);
            return "/category/category-update";
        }

        return "redirect:/categories/list";
    }

}
