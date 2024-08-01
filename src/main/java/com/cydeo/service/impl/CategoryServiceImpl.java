package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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
                .map(category -> {
                    CategoryDto categoryDto =  mapperUtil.convert(category, new CategoryDto());
                    if(!category.getProductList().stream().toList().isEmpty()){
                        categoryDto.setHasProduct(true);
                    }
                    return categoryDto;
                })
                .toList();
    }

    public CategoryDto findById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return mapperUtil.convert(category, new CategoryDto());
    }

    public CategoryDto saveCategory(CategoryDto category){
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        Category existingCategory = categoryRepository.findByDescriptionAndCompanyId(category.getDescription(), companyId);

        if(existingCategory != null){
            throw new IllegalArgumentException("Category already exists in Company.");
        }

        category.setCompany(companyService.findById(companyId));

        return mapperUtil.convert(categoryRepository.save(mapperUtil.convert(category, new Category())), new CategoryDto());
    }
    public boolean existsByDescription(String description) {
        Category category = categoryRepository.findByDescription(description);

        return category != null;
    }
    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));

        category.setIsDeleted(true);

        categoryRepository.save(category);
    }
}
