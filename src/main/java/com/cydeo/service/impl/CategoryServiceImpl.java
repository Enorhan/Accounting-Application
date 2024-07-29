package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil){
        this.categoryRepository = categoryRepository;
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
        return mapperUtil.convert(categoryRepository.save(category), new CategoryDto());
    }
}
