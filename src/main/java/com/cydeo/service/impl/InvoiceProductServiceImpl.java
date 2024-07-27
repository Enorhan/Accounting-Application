package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.cydeo.util.MapperUtil;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;
    private final UserService userService;
    private final InvoiceService invoiceService;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil, UserService userService, @Lazy InvoiceService invoiceService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
        this.invoiceService = invoiceService;
    }

    @Override
    public InvoiceProductDto findById(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + id));


        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> findAllByInvoiceId(Long id) {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findAllByInvoiceId(id);

        return invoiceProducts.stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(InvoiceProductDto invoiceProductDto, Long invoiceId) {
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        InvoiceDto invoiceDto = invoiceService.findById(invoiceId);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

//        invoiceProduct.setProduct();

        Long userId = userService.getCurrentUserId();

        invoiceProduct.setInvoice(invoice);
        invoiceProduct.setProfitLoss(BigDecimal.ZERO);
        invoiceProduct.setRemainingQuantity(10);
        invoiceProduct.setInsertDateTime(LocalDateTime.now());
        invoiceProduct.setLastUpdateDateTime(LocalDateTime.now());
        invoiceProduct.setInsertUserId(userId);
        invoiceProduct.setLastUpdateUserId(userId);


//        int quantity;
//        BigDecimal price;
//        int tax;
//        BigDecimal profitLoss;
//        int remainingQuantity;
//
//        @ManyToOne
//        Invoice invoice;
//
//        @ManyToOne
//        Product product;

        invoiceProductRepository.save(invoiceProduct);

    }

//    public void save(InvoiceDto invoiceDto, InvoiceType invoiceType) {
//        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());
//
//        CompanyDto companyDto = companyService.getCompanyDtoByLoggedInUser();
//        Company company = mapperUtil.convert(companyDto, new Company());
//
//        invoice.setCompany(company);
//
//        invoiceRepository.save(invoice);
//    }


}
