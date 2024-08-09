package com.cydeo.util;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.service.InvoiceProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class InvoiceUtils {
    private final InvoiceProductService invoiceProductService;

    public InvoiceUtils(InvoiceProductService invoiceProductService) {
        this.invoiceProductService = invoiceProductService;
    }

    public void calculateTotalsForInvoice(InvoiceDto invoiceDto) {
        Long invoiceId = invoiceDto.getId();
        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false);

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
}
