package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;

import java.util.List;

public interface ClientVendorService {
    List<ClientVendorDto> listAllClientVendors();
    List<ClientVendorDto> listAllClientVendorsByCompany();
    ClientVendorDto findById(Long id);
    List<ClientVendorDto> findAllByClientVendorTypeAndIsDeleted(ClientVendorType clientVendorType, Boolean isDeleted);
}
