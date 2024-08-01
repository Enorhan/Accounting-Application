package com.cydeo.converter;

import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryDTOConverter implements Converter<String,CategoryDto> {

    private final CategoryService categoryService;

    public CategoryDTOConverter(CategoryService categoryService){
        this.categoryService = categoryService;
    }
    public CategoryDto convert(String source) {

        if (source == null || source.isEmpty()) {
            return null;

        }

        Long id = Long.parseLong(source);
        return categoryService.findById(id);
    }
}
