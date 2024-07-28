package com.cydeo.service;

import com.cydeo.dto.InvoiceProductDto;

import java.util.List;

public interface InvoiceProductService {
    InvoiceProductDto findById(Long id);
    List<InvoiceProductDto> findAllByInvoiceIdAndIsDeleted(Long id, boolean isDeleted);
    void save(InvoiceProductDto invoiceProductDto, Long invoiceId);
    void delete(Long invoiceProductId);
    void removeInvoiceProduct(Long invoiceProductId);
}
