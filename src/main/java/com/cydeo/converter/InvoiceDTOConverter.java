package com.cydeo.converter;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.service.InvoiceService;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

@Component
public class InvoiceDTOConverter implements Converter<String, InvoiceDto> {

    private final InvoiceService invoiceService;

    public InvoiceDTOConverter(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public InvoiceDto convert(String source) {
        if (source == null || source.equals("")) {
            return null;
        }

        return invoiceService.findById(Long.valueOf(source));
    }
}
