package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ReportingService {


    List<InvoiceProductDto> getStockData();

    Map<String, BigDecimal> getMonthlyProfitLossData();


}
