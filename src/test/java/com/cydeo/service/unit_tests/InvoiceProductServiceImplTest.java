package com.cydeo.service.unit_tests;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.*;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exceptions.InvoiceProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.impl.InvoiceProductServiceImpl;
import com.cydeo.util.InvoiceUtils;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceProductServiceImplTest {
    @Mock
    InvoiceProductRepository invoiceProductRepository;

    @Mock
    InvoiceService invoiceService;

    @Mock
    CompanyService companyService;

    @Mock
    MapperUtil mapperUtil;

    @Mock
    InvoiceUtils invoiceUtils;


    @InjectMocks
    InvoiceProductServiceImpl invoiceProductService;

    @Test
    void findById_Test() {
        when(invoiceProductRepository.findById(anyLong())).thenReturn(Optional.of(new InvoiceProduct()));
        when(mapperUtil.convert(any(InvoiceProduct.class), any(InvoiceProductDto.class)))
                .thenReturn(TestDocumentInitializer.getInvoiceProduct());

        InvoiceProductDto result = invoiceProductService.findById(anyLong());

        InOrder inOrder = inOrder(invoiceProductRepository, mapperUtil);

        inOrder.verify(invoiceProductRepository, times(1)).findById(anyLong());
        inOrder.verify(mapperUtil).convert(any(InvoiceProduct.class), any(InvoiceProductDto.class));

        assertNotNull(result);
    }

    @Test
    void findByIdException_Test() {
        when(invoiceProductRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InvoiceProductNotFoundException.class, () -> {
            invoiceProductService.findById(anyLong());
        });

        verify(invoiceProductRepository, times(1)).findById(anyLong());
        verify(mapperUtil, never()).convert(any(), any());
    }

    @Test
    void findAllByInvoiceIdAndIsDeleted_Test() {
        when(invoiceProductRepository.findAllByInvoiceIdAndIsDeleted(anyLong(), anyBoolean())).thenReturn(List.of(new InvoiceProduct()));
        when(mapperUtil.convert(any(InvoiceProduct.class), any(InvoiceProductDto.class)))
                .thenReturn(TestDocumentInitializer.getInvoiceProduct());

        List<InvoiceProductDto> invoiceProductDtos = invoiceProductService.findAllByInvoiceIdAndIsDeleted(anyLong(), anyBoolean());

        assertEquals(1, invoiceProductDtos.size());
        assertEquals(BigDecimal.valueOf(55), invoiceProductDtos.get(0).getTotal());
    }

    @Test
    void getTotalCostAndSalesAndProfit_loss_Test() {
        Long companyId = TestDocumentInitializer.getUser("Manager").getId();
        InvoiceDto purchaseInvoiceDto = TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE);
        InvoiceDto salesInvoiceDto = TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.SALES);

        when(companyService.getCompanyIdByLoggedInUser()).thenReturn(companyId);
        when(invoiceService.findAllByInvoiceTypeAndInvoiceStatusAndCompanyIdAndIsDeleted(
                InvoiceType.PURCHASE, InvoiceStatus.APPROVED, companyId, false
        )).thenReturn(List.of(purchaseInvoiceDto));
        when(invoiceService.findAllByInvoiceTypeAndInvoiceStatusAndCompanyIdAndIsDeleted(
                InvoiceType.SALES, InvoiceStatus.APPROVED, companyId, false
        )).thenReturn(List.of(salesInvoiceDto));

        Map<String, BigDecimal> result = invoiceProductService.getTotalCostAndSalesAndProfit_loss();

        BigDecimal expectedTotalCost = purchaseInvoiceDto.getPrice();
        BigDecimal expectedTotalSales = salesInvoiceDto.getPrice();
        BigDecimal expectedProfitLoss = expectedTotalSales.subtract(expectedTotalCost);

        assertEquals(expectedTotalCost, result.get("totalCost"));
        assertEquals(expectedTotalSales, result.get("totalSales"));
        assertEquals(expectedProfitLoss, result.get("profitLoss"));

        verify(invoiceService, times(1)).findAllByInvoiceTypeAndInvoiceStatusAndCompanyIdAndIsDeleted(
                InvoiceType.PURCHASE, InvoiceStatus.APPROVED, companyId, false
        );
        verify(invoiceService, times(1)).findAllByInvoiceTypeAndInvoiceStatusAndCompanyIdAndIsDeleted(
                InvoiceType.SALES, InvoiceStatus.APPROVED, companyId, false
        );
    }
}
