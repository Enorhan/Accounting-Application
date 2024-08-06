package com.cydeo.service.unit_tests;

import com.cydeo.exceptions.ClientVendorNotFoundException;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.impl.ClientVendorServiceImpl;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientVendorServiceImplTest {

    @Mock
    private ClientVendorRepository clientVendorRepository;

    @Mock
    private MapperUtil mapperUtil;

    @InjectMocks
    private ClientVendorServiceImpl clientVendorService;

    @Test
    void findByIdException_Test() {

        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ClientVendorNotFoundException.class, () -> {
            clientVendorService.findById(anyLong());
        });

        verify(clientVendorRepository, times(1)).findById(anyLong());
        verify(mapperUtil, never()).convert(any(), any());
    }
}
