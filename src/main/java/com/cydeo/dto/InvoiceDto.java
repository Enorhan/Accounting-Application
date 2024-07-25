package com.cydeo.dto;

import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
