package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface InvoiceProductService {
    InvoiceProductDto findById(Long id);
    List<InvoiceProductDto> findAllByInvoiceId(Long id);
    void removeInvoiceProduct(Long invoiceProductId);
    void save(InvoiceProductDto invoiceProductDto, Long invoiceId);
    List<InvoiceDto> getLast3ApprovedInvoices();






    Map<String, BigDecimal> getTotalCostAndSalesAndProfit_loss();

}
