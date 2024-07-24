package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import org.springframework.core.convert.converter.Converter;

public interface InvoiceProductService {
    InvoiceProductDto findById(Long id);

}
