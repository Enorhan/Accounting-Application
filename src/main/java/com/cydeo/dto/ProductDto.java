package com.cydeo.dto;

import com.cydeo.enums.ProductUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    private Integer quantityInStock;

    @NotNull(message = "Low Limit Alert is a required field.")
    @Min(value = 1, message = "Low Limit Alert should be at least 1.")
    private Integer lowLimitAlert;

    @NotNull(message = "Product Unit is required field.")
    private ProductUnit productUnit;

    @NotNull(message = "Category is required field.")
    private CategoryDto category;

    private boolean hasInvoiceProduct;
}
