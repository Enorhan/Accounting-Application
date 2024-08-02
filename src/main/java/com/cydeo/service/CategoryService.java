package com.cydeo.service;
import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> listAllCategories();

    CategoryDto findById(Long id);

    CategoryDto saveCategory(CategoryDto category);

    List<CategoryDto>listAllCategoriesByCompany();
}
