package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;

import java.util.List;

public interface InvoiceProductService {
    InvoiceProductDto findById(Long id);
    List<InvoiceProductDto> findAllByInvoiceId(Long id);
    void removeInvoiceProduct(Long invoiceProductId);
    void save(InvoiceProductDto invoiceProductDto, Long invoiceId);
}
