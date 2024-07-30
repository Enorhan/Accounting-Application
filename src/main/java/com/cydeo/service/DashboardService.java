package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String,BigDecimal> getTotalCostAndSalesAndProfit_loss();

    List<InvoiceDto> getLast3ApprovedInvoices();
}
