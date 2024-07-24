package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceType;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType);
    InvoiceDto findById(Long id);

}
