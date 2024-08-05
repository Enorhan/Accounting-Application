package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exceptions.InvoiceProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
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

    public InvoiceProductServiceImpl(InvoiceProductRepository invoiceProductRepository, MapperUtil mapperUtil, @Lazy InvoiceService invoiceService, CompanyService companyService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.mapperUtil = mapperUtil;
        this.invoiceService = invoiceService;
        this.companyService = companyService;
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
        Long loggedInUser = companyService.getCompanyIdByLoggedInUser();

        //to retrieve purchased invoices total cost
        List<InvoiceProduct> purchasedInvoices = invoiceProductRepository.findAll().stream()
                .filter(p -> p.getInvoice().getCompany().getId().equals(loggedInUser))
                .filter(invoice -> invoice.getInvoice().getInvoiceType() == InvoiceType.PURCHASE && invoice.getInvoice().getInvoiceStatus() == InvoiceStatus.APPROVED)
                .collect(Collectors.toList());

        BigDecimal totalCost = BigDecimal.valueOf(0);
        for (InvoiceProduct invoice : purchasedInvoices) {
            int quantity = invoice.getQuantity();
            BigDecimal price = invoice.getPrice();
            int tax = invoice.getTax();

            totalCost = totalCost.add(price.multiply(BigDecimal.valueOf(quantity)).add(price.multiply(BigDecimal.valueOf(quantity)).divide(BigDecimal.valueOf(tax))));
        }

        //to retrieve sales invoices total
        List<InvoiceProduct> salesInvoices = invoiceProductRepository.findAll().stream()
                .filter(p -> p.getInvoice().getCompany().getId().equals(loggedInUser))
                .filter(invoice -> invoice.getInvoice().getInvoiceType() == InvoiceType.SALES && invoice.getInvoice().getInvoiceStatus() == InvoiceStatus.APPROVED)
                .collect(Collectors.toList());

        BigDecimal totalSalesCost = BigDecimal.valueOf(0);
        for (InvoiceProduct invoice : salesInvoices) {
            int quantity = invoice.getQuantity();
            BigDecimal price = invoice.getPrice();
            int tax = invoice.getTax();

            totalSalesCost = totalSalesCost.add(price.multiply(BigDecimal.valueOf(quantity)).add(price.multiply(BigDecimal.valueOf(quantity)).divide(BigDecimal.valueOf(tax))));
        }

        //to calculate total profit or loss
        List<BigDecimal> profitLoss = invoiceProductRepository.findAll().stream()
                .filter(p -> p.getInvoice().getCompany().getId().equals(loggedInUser))
                .map(invoice -> invoice.getProfitLoss()).collect(Collectors.toList());

        BigDecimal totalProfitLoss = BigDecimal.valueOf(0);
        for (BigDecimal profit_Loss : profitLoss) {

            totalProfitLoss = totalProfitLoss.add(profit_Loss);
        }


        Map<String, BigDecimal> map = new HashMap<>();
        map.put("totalCost", totalCost);
        map.put("totalSales", totalSalesCost);
        map.put("profitLoss", totalProfitLoss);

        return map;
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
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        List<Invoice> invoices = invoiceService.findTop3ApprovedInvoicesByCompanyId(companyId, InvoiceStatus.APPROVED);
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.find3LastApprovedTransactionDesc(companyId);

        List<InvoiceDto> invoiceDtos = invoices.stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .collect(Collectors.toList());


        for (int i = 0; i < 3; i++) {
            //Quantity of products
            BigDecimal quantity = BigDecimal.valueOf(invoiceProducts.get(i).getQuantity());
            //taxRate of products
            BigDecimal taxRate = BigDecimal.valueOf(invoiceProducts.get(i).getTax());
            //unit price of products
            BigDecimal unitPrice = invoiceProducts.get(i).getPrice();

            invoiceDtos.get(i).setPrice(quantity.multiply(unitPrice));
            invoiceDtos.get(i).setTax(quantity.multiply(unitPrice).multiply(taxRate.divide(BigDecimal.valueOf(100))));
            invoiceDtos.get(i).setTotal(quantity.multiply(unitPrice).add(quantity.multiply(unitPrice).divide(taxRate)));
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
