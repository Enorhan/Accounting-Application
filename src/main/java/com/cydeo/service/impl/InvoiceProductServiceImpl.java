package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.Product;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;
import com.cydeo.util.MapperUtil;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;
    private final ProductService productService;
    private final InvoiceService invoiceService;
    private final UserService userService;


    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil, ProductService productService, InvoiceService invoiceService, UserService userService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.productService = productService;
        this.invoiceService = invoiceService;
        this.userService = userService;
    }

    @Override
    public InvoiceProductDto findById(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + id));


        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> findAllByInvoiceId(Long id) {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findAllByInvoiceIdAndIsDeleted(id, false);

        return invoiceProducts.stream()
                .map(invoiceProduct -> {
                    InvoiceProductDto dto = mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
                    BigDecimal totalPrice = calculateTotalPrice(dto.getPrice(), dto.getQuantity(), dto.getTax());
                    dto.setTotal(totalPrice);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public void removeInvoiceProduct(Long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(invoiceProductId)
                .orElseThrow(() -> new NoSuchElementException("Invoice product not found with id: " + invoiceProductId));
        invoiceProduct.setIsDeleted(true);
        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public void save(InvoiceProductDto invoiceProductDto, Long invoiceId) {
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        InvoiceDto invoiceDto = invoiceService.findById(invoiceId);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        Long userId = userService.getCurrentUserId();

        invoiceProduct.setInvoice(invoice);
        invoiceProduct.setProfitLoss(BigDecimal.ZERO);
        invoiceProduct.setRemainingQuantity(10);
        invoiceProduct.setInsertDateTime(LocalDateTime.now());
        invoiceProduct.setLastUpdateDateTime(LocalDateTime.now());
        invoiceProduct.setInsertUserId(userId);
        invoiceProduct.setLastUpdateUserId(userId);

        invoiceProductRepository.save(invoiceProduct);

    }


    private BigDecimal calculateTotalPrice(BigDecimal price, int quantity, int tax) {
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal taxAmount = totalPrice.multiply(BigDecimal.valueOf(tax)).divide(BigDecimal.valueOf(100));
        return totalPrice.add(taxAmount);
    }
}
