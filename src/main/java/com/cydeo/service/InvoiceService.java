package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType);
    InvoiceDto findById(Long id);
    String getNewPurchaseInvoiceNumberId();
    void save(InvoiceDto invoiceDto, InvoiceType invoiceType);
    InvoiceDto update(InvoiceDto invoiceDto, Long invoiceId);
    String createNewSalesInvoiceNo();
    void delete(Long id);
    void approveSalesInvoice(Long id);
    void approvePurchaseInvoice(Long id);
    List<Invoice> findTop3InvoicesByInvoiceStatus(InvoiceStatus invoiceStatus);
    InvoiceDto findByInvoiceNo(String invoiceNo);
    Boolean isQuantityAvailable(InvoiceProductDto invoiceProductDto, BindingResult bindingResult);
}
