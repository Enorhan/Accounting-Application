package com.cydeo.service;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ReportingService {


    List<InvoiceProductDto> getStockData();


}
