package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.exceptions.ClientVendorNotFoundException;

import java.util.List;

public interface ClientVendorService {
    List<ClientVendorDto> listAllClientVendorsByCompany();
    List<ClientVendorDto> listAllVendorsByCompany();
    ClientVendorDto findById(Long id) throws ClientVendorNotFoundException;
    List<ClientVendorDto> findAllByCurrentCompanyClientVendorTypeAndIsDeleted(ClientVendorType clientVendorType, Boolean isDeleted);

    boolean existsByName(String clientVendorName);

    ClientVendorDto createClientVendor(ClientVendorDto clientVendorDto);

    boolean isHasInvoices(Long id);

    void deleteClientVendor(Long id) throws ClientVendorNotFoundException;

    List<ClientVendorDto> findAll();

    ClientVendorDto updateClientVendor(Long id, ClientVendorDto clientVendorDTO) throws ClientVendorNotFoundException;

    List<ClientVendorDto> listAllClientVendorsWithInvoiceStatusByCompany();
}