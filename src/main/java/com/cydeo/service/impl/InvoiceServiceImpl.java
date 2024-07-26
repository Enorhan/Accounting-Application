package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.CompanyService;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType) {
        Long companyId = securityService.getLoggedInUser().getCompany().getId();

        return invoiceRepository.findAllByInvoiceTypeAndCompanyIdOrderByInvoiceNoDesc(invoiceType, companyId).stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found with id: " + id));

        return mapperUtil.convert(invoice, new InvoiceDto());
    }

    @Override
    public String getNewPurchaseInvoiceNumberId() {
        Long companyId = securityService.getLoggedInUser().getCompany().getId();

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

        nextPurchaseInvoiceId =  Long.parseLong(dividedLastInvoiceNumber[1]) + 1;

        nextPurchaseInvoiceNumberId = String.format("P-%03d", nextPurchaseInvoiceId);

        return nextPurchaseInvoiceNumberId;
    }

    @Override
    public void save(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        Long userId = securityService.getLoggedInUser().getId();
        CompanyDto companyDto = securityService.getLoggedInUser().getCompany();
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
    @Transactional
    public String createNewSalesInvoiceNo() {

        String company=securityService.getLoggedInUser().getCompany().getTitle();
        Invoice latestInvoice = invoiceRepository.findTopSalesInvoice(company);

        if (latestInvoice == null) {
            return "S-001";
        } else {

            String latestInvoiceNo = latestInvoice.getInvoiceNo();
            int latestNumber = Integer.parseInt(latestInvoiceNo.substring(2));

            return "S-" + String.format("%03d", latestNumber+1);
        }
    }

}
