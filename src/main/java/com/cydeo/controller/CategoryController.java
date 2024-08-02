package com.cydeo.controller;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
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
    public String updateCategory(Model model, @ModelAttribute("newCategory") Category category){
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
