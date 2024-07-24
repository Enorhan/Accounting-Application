package com.cydeo.dto;

import com.cydeo.enums.ProductUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private Long id;

    private String name;

    private Integer quantityInStock;

    private Integer lowLimitAlert;

    private ProductUnit productUnit;

    private CategoryDto category;

    private boolean hasProduct;
}
