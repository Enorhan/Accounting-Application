package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.ReportingService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final MapperUtil mapperUtil;
    private final InvoiceProductRepository invoiceProductRepository;

    public ReportingServiceImpl(MapperUtil mapperUtil, InvoiceProductRepository invoiceProductRepository) {
        this.mapperUtil = mapperUtil;
        this.invoiceProductRepository = invoiceProductRepository;
    }


    @Override
    public List<InvoiceProductDto> getStockData() {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findAll();
        return invoiceProductRepository.findAllByOrderByInvoiceDesc().stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }


}
