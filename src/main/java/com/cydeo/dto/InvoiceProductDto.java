package com.cydeo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceProductDto {


    Long id;

    @NotNull(message = "Quantity is a required field.")
    @Min(value = 1, message = "Quantity cannot be less than 1.")
    @Max(value = 100, message = "Quantity cannot be greater than 100.")
    Integer quantity;

    @NotNull(message = "Price is a required field.")
    @DecimalMin(value = "1.00", message = "Price should be at least $1.")
    BigDecimal price;

    @NotNull(message = "Tax is a required field.")
    @Min(value = 0, message = "Tax should be between 0% and 20%.")
    @Max(value = 20, message = "Tax should be between 0% and 20%.")
    Integer tax;

    BigDecimal total;
    BigDecimal profitLoss;
    Integer remainingQuantity;

    InvoiceDto invoice;

    @NotNull(message = "Product is a required field.")
    ProductDto product;
}