package com.cydeo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceProductDto {


    Long id;
    Integer quantity;
    BigDecimal price;
    Integer tax;
    BigDecimal total;
    BigDecimal profitLoss;
    Integer remainingQuantity;
    InvoiceDto invoice;
    ProductDto product;
}