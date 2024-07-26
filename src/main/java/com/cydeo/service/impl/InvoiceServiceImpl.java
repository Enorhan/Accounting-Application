package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.*;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final UserService userService;
    private final InvoiceProductService invoiceProductService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, CompanyService companyService, UserService userService, InvoiceProductService invoiceProductService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.userService = userService;
        this.invoiceProductService = invoiceProductService;
    }

    private void calculateTotalsForInvoice(InvoiceDto invoiceDto) {
        Long invoiceId = invoiceDto.getId();
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findAllByInvoiceId(invoiceId);

        BigDecimal totalPriceWithoutTax = invoiceProducts.stream()
                .map(invoiceProduct -> invoiceProduct.getPrice().multiply(BigDecimal.valueOf(invoiceProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = invoiceProducts.stream()
                .map(invoiceProduct -> invoiceProduct.getPrice()
                        .multiply(BigDecimal.valueOf(invoiceProduct.getTax()))
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(invoiceProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPriceWithTax = totalPriceWithoutTax.add(totalTax);

        invoiceDto.setPrice(totalPriceWithoutTax);
        invoiceDto.setTax(totalTax);
        invoiceDto.setTotal(totalPriceWithTax);
    }

    @Override
    public List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType) {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        List<InvoiceDto> invoiceDtos = invoiceRepository.
                findAllByInvoiceTypeAndCompanyIdOrderByInvoiceNoDesc(invoiceType, companyId).stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .collect(Collectors.toList());

        for (InvoiceDto eachInvoiceDto : invoiceDtos) {
            calculateTotalsForInvoice(eachInvoiceDto);
        }

        return invoiceDtos;
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + id));

        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public String getNewPurchaseInvoiceNumberId() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        String lastPurchaseInvoiceNumberId = invoiceRepository
                .findAllByInvoiceTypeAndCompanyIdOrderByInvoiceNoDesc(InvoiceType.PURCHASE, companyId)
                .get(0)
                .getInvoiceNo();

        if (lastPurchaseInvoiceNumberId == null) {
            return "P-001";
        }

        String nextPurchaseInvoiceNumberId;
        long nextPurchaseInvoiceId;

        String[] dividedLastInvoiceNumber = lastPurchaseInvoiceNumberId.split("-");

        nextPurchaseInvoiceId = Long.parseLong(dividedLastInvoiceNumber[1]) + 1;

        nextPurchaseInvoiceNumberId = String.format("P-%03d", nextPurchaseInvoiceId);

        return nextPurchaseInvoiceNumberId;
    }

    @Override
    public void save(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        Long userId = userService.getCurrentUserId();
        CompanyDto companyDto = companyService.getCompanyDtoByLoggedInUser();
        Company company = mapperUtil.convert(companyDto, new Company());

        invoice.setInvoiceType(invoiceType);
        invoice.setInsertDateTime(LocalDateTime.now());
        invoice.setLastUpdateDateTime(LocalDateTime.now());
        invoice.setInsertUserId(userId);
        invoice.setLastUpdateUserId(userId);
        invoice.setCompany(company);
        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);

        invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceDto update(InvoiceDto invoiceDto, Long id) {
        Invoice oldInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + id));
        invoiceDto.setId(id);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        Long userId = userService.getCurrentUserId();

        invoice.setLastUpdateDateTime(LocalDateTime.now());
        invoice.setLastUpdateUserId(userId);

        invoice.setInsertDateTime(oldInvoice.getInsertDateTime());
        invoice.setInsertUserId(oldInvoice.getInsertUserId());
        invoice.setInvoiceStatus(oldInvoice.getInvoiceStatus());
        invoice.setInvoiceType(oldInvoice.getInvoiceType());
        invoice.setCompany(oldInvoice.getCompany());

        invoiceRepository.save(invoice);

        return findById(invoiceDto.getId());
    }


    @Override
    @Transactional
    public String createNewSalesInvoiceNo() {

        String company = companyService.getCurrentCompanyTitle();
        Invoice latestInvoice = invoiceRepository.findTopSalesInvoice(company);

        if (latestInvoice == null) {
            return "S-001";
        } else {

            String latestInvoiceNo = latestInvoice.getInvoiceNo();
            int latestNumber = Integer.parseInt(latestInvoiceNo.substring(2));

            return "S-" + String.format("%03d", latestNumber + 1);
        }
    }

}
