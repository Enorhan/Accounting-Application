package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exceptions.InvoiceProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.util.InvoiceUtils;
import org.springframework.stereotype.Service;
import com.cydeo.util.MapperUtil;
import org.springframework.context.annotation.Lazy;


import java.math.BigDecimal;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceProductServiceImpl implements InvoiceProductService {
    private final InvoiceProductRepository invoiceProductRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final InvoiceService invoiceService;
    private final InvoiceUtils invoiceUtils;
    private final InvoiceRepository invoiceRepository;

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil, @Lazy InvoiceService invoiceService, CompanyService companyService, @Lazy InvoiceUtils invoiceUtils, InvoiceRepository invoiceRepository) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.invoiceService = invoiceService;
        this.companyService = companyService;
        this.invoiceUtils = invoiceUtils;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public InvoiceProductDto findById(Long id) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(id)
                .orElseThrow(() -> new InvoiceProductNotFoundException("Invoice not found with id: " + id));

        return mapperUtil.convert(invoiceProduct, new InvoiceProductDto());
    }

    @Override
    public List<InvoiceProductDto> findAllByInvoiceIdAndIsDeleted(Long id, boolean isDeleted) {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findAllByInvoiceIdAndIsDeleted(id, isDeleted);

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
    public Map<String, BigDecimal> getTotalCostAndSalesAndProfit_loss() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        // Purchase Invoice Calculations
        List<InvoiceDto> purchaseInvoiceDtos = invoiceRepository.
                findAllByInvoiceTypeAndCompanyIdAndIsDeletedAndInvoiceStatus(InvoiceType.PURCHASE, companyId, false, InvoiceStatus.APPROVED)
                .stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .toList();

        BigDecimal totalCost = BigDecimal.valueOf(0);

        for (InvoiceDto eachInvoiceDto : purchaseInvoiceDtos) {
            invoiceUtils.calculateTotalsForInvoice(eachInvoiceDto);
            totalCost = totalCost.add(eachInvoiceDto.getTotal());
        }

        // Sale Invoice Calculations
        List<InvoiceDto> salesInvoiceDtos = invoiceRepository.
                findAllByInvoiceTypeAndCompanyIdAndIsDeletedAndInvoiceStatus(InvoiceType.SALES, companyId, false, InvoiceStatus.APPROVED)
                .stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .toList();

        BigDecimal totalSalesCost = BigDecimal.valueOf(0);

        for (InvoiceDto eachInvoiceDto : salesInvoiceDtos) {
            invoiceUtils.calculateTotalsForInvoice(eachInvoiceDto);
            totalSalesCost = totalSalesCost.add(eachInvoiceDto.getTotal());
        }

        Map<String, BigDecimal> calculations = new HashMap<>();
        calculations.put("totalCost", totalCost);
        calculations.put("totalSales", totalSalesCost);
        calculations.put("profitLoss", totalCost.subtract(totalSalesCost));

        return calculations;
    }

    @Override
    public Map<String, BigDecimal> getMonthlyProfitLoss() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        List<InvoiceProduct> salesInvoices = invoiceProductRepository.findAll().stream()
                .filter(p -> p.getInvoice().getCompany().getId().equals(companyId))
                .filter(invoice -> invoice.getInvoice().getInvoiceType() == InvoiceType.SALES && invoice.getInvoice().getInvoiceStatus() == InvoiceStatus.APPROVED)
                .collect(Collectors.toList());

        return salesInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoiceProduct -> invoiceProduct.getInvoice().getDate().getMonth()
                                .getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        Collectors.reducing(BigDecimal.ZERO, InvoiceProduct::getProfitLoss, BigDecimal::add)
                ));
    }

    @Override
    public List<InvoiceDto> getLast3ApprovedInvoices() {
        List<Invoice> invoices = invoiceService.findTop3InvoicesByInvoiceStatus(InvoiceStatus.APPROVED);

        List<InvoiceDto> invoiceDtos = invoices.stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .collect(Collectors.toList());

        for (InvoiceDto eachInvoiceDto : invoiceDtos) {
            invoiceUtils.calculateTotalsForInvoice(eachInvoiceDto);
        }

        return invoiceDtos;
    }

    @Override
    public void save(InvoiceProductDto invoiceProductDto, Long invoiceId) {
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        InvoiceDto invoiceDto = invoiceService.findById(invoiceId);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        invoiceProduct.setInvoice(invoice);

        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public void delete(Long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(invoiceProductId)
                .orElseThrow(() -> new InvoiceProductNotFoundException("Invoice product not found with id: " + invoiceProductId));

        invoiceProduct.setIsDeleted(true);

        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public void deleteByInvoiceId(Long invoiceId) {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findAllByInvoiceIdAndIsDeleted(invoiceId, false);

        for (InvoiceProduct invoiceProduct : invoiceProducts) {
            delete(invoiceProduct.getId());
        }
    }

    @Override
    public void removeInvoiceProduct(Long invoiceProductId) {
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(invoiceProductId)
                .orElseThrow(() -> new InvoiceProductNotFoundException("Invoice product not found with id: " + invoiceProductId));

        invoiceProduct.setIsDeleted(true);

        invoiceProductRepository.save(invoiceProduct);
    }

    private BigDecimal calculateTotalPrice(BigDecimal price, int quantity, int tax) {
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal taxAmount = totalPrice.multiply(BigDecimal.valueOf(tax)).divide(BigDecimal.valueOf(100));
        return totalPrice.add(taxAmount);
    }

    @Override
    public void saveSalesInvoice(InvoiceProductDto invoiceProductDto) {
        InvoiceProduct invoiceProduct = mapperUtil.convert(invoiceProductDto, new InvoiceProduct());
        invoiceProductRepository.save(invoiceProduct);
    }

    @Override
    public List<InvoiceProductDto> findAll() {
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findAll();
        return invoiceProducts.stream()
                .map(invoiceProduct -> mapperUtil.convert(invoiceProduct, new InvoiceProductDto()))
                .collect(Collectors.toList());
    }
}
