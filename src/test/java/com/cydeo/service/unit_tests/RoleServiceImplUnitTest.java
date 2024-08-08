package com.cydeo.service.unit_tests;


import com.cydeo.exceptions.RoleNotFoundException;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.impl.RoleServiceImpl;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplUnitTest {
    @Mock
    private RoleRepository roleRepository;
//    @Mock
//    private MapperUtil mapperUtil;
@Spy
MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void findById_Test() {

        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            roleService.findById(anyLong());
        });

        verify(roleRepository, times(1)).findById(anyLong());
        verify(mapperUtil, never()).convert(any(), any());
    }

}
