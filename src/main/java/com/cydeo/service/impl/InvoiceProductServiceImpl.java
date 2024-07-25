package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import org.springframework.stereotype.Service;
import com.cydeo.util.MapperUtil;


import java.util.NoSuchElementException;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
    }



    @Override
    public InvoiceProductDto findById(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + id));


        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

}
