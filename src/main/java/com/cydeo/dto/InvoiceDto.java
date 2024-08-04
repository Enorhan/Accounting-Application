package com.cydeo.dto;

import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {
    Long id;
    String invoiceNo;
    InvoiceStatus invoiceStatus;
    InvoiceType invoiceType;
    LocalDate date;
    CompanyDto company;

    @NotNull
    ClientVendorDto clientVendor;
    BigDecimal price;
    BigDecimal tax;
    BigDecimal total;
}
