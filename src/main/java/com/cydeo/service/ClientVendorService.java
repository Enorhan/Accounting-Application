package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import java.util.List;
public interface ClientVendorService {

    List<ClientVendorDto> findAll();
    ClientVendorDto findById(Long id);

}
