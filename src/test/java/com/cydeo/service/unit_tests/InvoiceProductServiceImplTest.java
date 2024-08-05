package com.cydeo.service.unit_tests;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.exceptions.InvoiceProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.service.impl.InvoiceProductServiceImpl;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceProductServiceImplTest {
    @Mock
    InvoiceProductRepository invoiceProductRepository;

    @Mock
    MapperUtil mapperUtil;

    @InjectMocks
    InvoiceProductServiceImpl invoiceProductService;

    @Test
    void findById_Test() {
        when(invoiceProductRepository.findById(anyLong())).thenReturn(Optional.of(new InvoiceProduct()));
        when(mapperUtil.convert(any(InvoiceProduct.class), any(InvoiceProductDto.class)))
                .thenReturn(new InvoiceProductDto());

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
}
