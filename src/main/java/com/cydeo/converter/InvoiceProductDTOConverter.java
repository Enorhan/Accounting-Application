package com.cydeo.converter;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import org.springframework.core.convert.converter.Converter;

public class InvoiceProductDTOConverter implements Converter<Long, InvoiceProductDto> {
    private final InvoiceProductService invoiceProductService;

    public InvoiceProductDTOConverter(InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

    @Override
    public InvoiceProductDto convert(Long source) {
        if (source == null) {
            return null;
        }

        return invoiceProductService.findById(source);
    }
}
