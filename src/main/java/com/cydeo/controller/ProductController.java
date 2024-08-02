package com.cydeo.controller;

import com.cydeo.dto.ProductDto;
import com.cydeo.enums.ProductUnit;
import com.cydeo.service.CategoryService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public String getAllProducts(Model model){

        model.addAttribute("products",productService.findAllByCurrentCompany());

        return "product/product-list";
    }

    @GetMapping("/create")
    public String createProduct(Model model){

        model.addAttribute("newProduct",new ProductDto());
        model.addAttribute("categories",categoryService.listAllCategoriesByCompany());
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));

        return "product/product-create";
    }

    @PostMapping("/create")
    public String insertProduct(@Valid @ModelAttribute("newProduct") ProductDto productDto,
                                BindingResult bindingResult,Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("categories",categoryService.listAllCategoriesByCompany());
            model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
            return "product/product-create";
        }
        productService.save(productDto);
        return "redirect:/products/list";
    }

    @GetMapping("/update/{productId}")
    public String editProduct(@PathVariable("productId") Long productId,Model model){

        model.addAttribute("product",productService.findById(productId));
        model.addAttribute("categories",categoryService.listAllCategoriesByCompany());
        model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));

        return "product/product-update";
    }

    @PostMapping("/update/{productId}")
    public String updateProduct(@PathVariable("productId") Long productId,
                                @Valid @ModelAttribute("product") ProductDto productDto,
                                BindingResult bindingResult, Model model) {

        productDto.setId(productId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.listAllCategoriesByCompany());
            model.addAttribute("productUnits", Arrays.asList(ProductUnit.values()));
            return "product/product-update";
        }

        productService.update(productDto, productId);

        return "redirect:/products/list";
    }

    @GetMapping("/delete/{productId}")
    public String deleteProduct(@PathVariable("productId")Long productId){
        if (!(productService.hasInvoice(productId) | productService.isInStock(productId))){
            productService.delete(productId);
        }
        return "redirect:/products/list";
    }

}
