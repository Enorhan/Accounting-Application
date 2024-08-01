package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;

import java.util.List;

public interface ClientVendorService {
    List<ClientVendorDto> listAllClientVendors();
    List<ClientVendorDto> listAllClientVendorsByCompany();
    ClientVendorDto findById(Long id);
    List<ClientVendorDto> findAllByCurrentCompanyClientVendorTypeAndIsDeleted(ClientVendorType clientVendorType, Boolean isDeleted);

    boolean existsByName(String clientVendorName);

    ClientVendorDto createClientVendor(ClientVendorDto clientVendorDto);

    boolean isHasInvoices(Long id);

    void deleteClientVendor(Long id);

    List<ClientVendorDto> findAll();

    ClientVendorDto updateClientVendor(Long id, ClientVendorDto clientVendorDTO);

    List<ClientVendorDto> listAllClientVendorsWithInvoiceStatusByCompany();
}
