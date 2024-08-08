package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface InvoiceProductService {
    InvoiceProductDto findById(Long id);
    List<InvoiceProductDto> findAllByInvoiceIdAndIsDeleted(Long id, boolean isDeleted);
    void save(InvoiceProductDto invoiceProductDto, Long invoiceId);
    void delete(Long invoiceProductId);
    void deleteByInvoiceId(Long invoiceId);
    void removeInvoiceProduct(Long invoiceProductId);
    List<InvoiceDto> getLast3ApprovedInvoices();
    Map<String, BigDecimal> getTotalCostAndSalesAndProfit_loss();
    Map<String, BigDecimal> getMonthlyProfitLoss();
    List<InvoiceProductDto>findAll();
    void saveSalesInvoice(InvoiceProductDto invoiceProductDto);
}
