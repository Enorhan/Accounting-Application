package com.cydeo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceProductDto {
    Long id;
    @NotNull
    Integer quantity;
    @NotNull
    BigDecimal price;
    @NotNull
    Integer tax;
    BigDecimal total;
    BigDecimal profitLoss;
    Integer remainingQuantity;
    InvoiceDto invoice;
    ProductDto product;
}