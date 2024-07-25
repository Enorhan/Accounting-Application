package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceType;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.InvoiceService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType) {
        return invoiceRepository.findAllByInvoiceTypeOrderByInvoiceNoDesc(invoiceType).stream()
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
        String lastPurchaseInvoiceNumberId = invoiceRepository
                .findAllByInvoiceTypeOrderByInvoiceNoDesc(InvoiceType.PURCHASE)
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
}
