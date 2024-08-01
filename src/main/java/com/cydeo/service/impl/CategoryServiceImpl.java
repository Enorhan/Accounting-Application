package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CompanyService companyService;
    private final MapperUtil mapperUtil;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CompanyService companyService, MapperUtil mapperUtil){
        this.categoryRepository = categoryRepository;
        this.companyService = companyService;
        this.mapperUtil = mapperUtil;
    }

    public List<CategoryDto> listAllCategories(){
        return categoryRepository.findAll().stream()
                .map(category -> mapperUtil.convert(category, new CategoryDto()))
                .collect(Collectors.toList());
    }

    public CategoryDto findById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return mapperUtil.convert(category, new CategoryDto());
    }

    public CategoryDto saveCategory(Category category){
        Long id = companyService.getCompanyIdByLoggedInUser();
        category.setCompany(mapperUtil.convert(companyService.findById(id), new Company()));

        return mapperUtil.convert(categoryRepository.save(category), new CategoryDto());
    }

    @Override
    public List<CategoryDto> listAllCategoriesByCompany() {
        List<Category> categories=categoryRepository.findAllByCompanyId(companyService.getCompanyIdByLoggedInUser());
        return mapperUtil.convert(categories,new ArrayList<>());
    }
}
