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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
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

    @Spy
    MapperUtil mapperUtilSpy = new MapperUtil(new ModelMapper());

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

    @Test
    void getMonthlyProfitLoss_Test() {
        InvoiceProduct janInvoiceProduct = mapperUtilSpy.convert(TestDocumentInitializer.getInvoiceProduct(), new InvoiceProduct());
        janInvoiceProduct.setProfitLoss(BigDecimal.valueOf(100));
        janInvoiceProduct.getInvoice().setDate(LocalDate.of(2023, 1, 15));

        InvoiceProduct janInvoiceProduct2 = mapperUtilSpy.convert(TestDocumentInitializer.getInvoiceProduct(), new InvoiceProduct());
        janInvoiceProduct2.setProfitLoss(BigDecimal.valueOf(200));
        janInvoiceProduct2.getInvoice().setDate(LocalDate.of(2023, 1, 12));

        InvoiceProduct febInvoiceProduct = mapperUtilSpy.convert(TestDocumentInitializer.getInvoiceProduct(), new InvoiceProduct());
        febInvoiceProduct.setProfitLoss(BigDecimal.valueOf(200));
        febInvoiceProduct.getInvoice().setDate(LocalDate.of(2023, 2, 20));

        List<InvoiceProduct> mockInvoiceProducts = List.of(janInvoiceProduct, janInvoiceProduct2, febInvoiceProduct);

        when(invoiceProductRepository
                .findAllByInvoiceInvoiceStatusAndInvoiceInvoiceTypeAndInvoiceCompanyIdAndIsDeleted(
                        InvoiceStatus.APPROVED, InvoiceType.SALES, 0L, false
                )
        ).thenReturn(mockInvoiceProducts);

        Map<String, BigDecimal> monthlyProfitLoss = invoiceProductService.getMonthlyProfitLoss();

        assertEquals(janInvoiceProduct.getProfitLoss().add(janInvoiceProduct2.getProfitLoss()), monthlyProfitLoss.get("January"));
        assertEquals(febInvoiceProduct.getProfitLoss(), monthlyProfitLoss.get("February"));

        verify(invoiceProductRepository, times(1))
                .findAllByInvoiceInvoiceStatusAndInvoiceInvoiceTypeAndInvoiceCompanyIdAndIsDeleted(
                        InvoiceStatus.APPROVED, InvoiceType.SALES, 0L, false);
    }

    @Test
    void getLast3ApprovedInvoices_Test() {
        InvoiceDto invoiceDto1 = TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE);
        InvoiceDto invoiceDto2 = TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.SALES);
        InvoiceDto invoiceDto3 = TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE);

        Invoice invoice1 = mapperUtilSpy.convert(invoiceDto1, new Invoice());
        Invoice invoice2 = mapperUtilSpy.convert(invoiceDto2, new Invoice());
        Invoice invoice3 = mapperUtilSpy.convert(invoiceDto3, new Invoice());

        when(invoiceService.findTop3InvoicesByInvoiceStatus(InvoiceStatus.APPROVED)).thenReturn(List.of(invoice1, invoice2, invoice3));
        when(mapperUtil.convert(any(), any())).thenReturn(TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE));

        List<InvoiceDto> last3ApprovedInvoices = invoiceProductService.getLast3ApprovedInvoices();

        assertNotNull(last3ApprovedInvoices);
        assertEquals(3, last3ApprovedInvoices.size());

        for (InvoiceDto invoiceDto : last3ApprovedInvoices) {
            assertNotNull(invoiceDto.getPrice());
            assertNotNull(invoiceDto.getTax());
            assertNotNull(invoiceDto.getTotal());
        }

        verify(invoiceUtils, times(3)).calculateTotalsForInvoice(any(InvoiceDto.class));
    }

    @Test
    void save_Test() {
        InvoiceProductDto invoiceProductDto = TestDocumentInitializer.getInvoiceProduct();
        InvoiceDto invoiceDto = TestDocumentInitializer.getInvoice(InvoiceStatus.APPROVED, InvoiceType.PURCHASE);

        Invoice invoice = mapperUtilSpy.convert(invoiceDto, new Invoice());
        InvoiceProduct invoiceProduct = mapperUtilSpy.convert(invoiceProductDto, new InvoiceProduct());

        when(mapperUtil.convert(any(InvoiceProductDto.class), any(InvoiceProduct.class))).thenReturn(invoiceProduct);

        when(invoiceService.findById(anyLong())).thenReturn(invoiceDto);

        invoiceProductService.save(invoiceProductDto, anyLong());

        verify(invoiceProductRepository, times(1)).save(invoiceProduct);
        assertEquals(invoice.getId(), invoiceProduct.getInvoice().getId());
    }
}
