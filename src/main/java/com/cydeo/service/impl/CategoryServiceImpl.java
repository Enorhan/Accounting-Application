package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.util.MapperUtil;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil){
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
    }

    public List<CategoryDto> listAllCategories(){
        return categoryRepository.listAllCategories().stream()
                .map(category -> mapperUtil.convert(category, new CategoryDto()))
                .collect(Collectors.toList());
    }
}
