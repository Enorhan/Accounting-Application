package com.cydeo.service;
import com.cydeo.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> listAllCategories();

    CategoryDto findById(Long id);


    List<CategoryDto>listAllCategoriesByCompany();
    CategoryDto saveCategory(CategoryDto category);
}
