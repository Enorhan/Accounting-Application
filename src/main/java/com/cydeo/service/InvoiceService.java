package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType);
    InvoiceDto findById(Long id);
    String getNewPurchaseInvoiceNumberId();
    void save(InvoiceDto invoiceDto, InvoiceType invoiceType);
    String createNewSalesInvoiceNo();
    void delete(Long id);

    List<Invoice> findTop3ApprovedInvoicesByCompanyId(Long companyId, InvoiceStatus invoiceStatus);
}
