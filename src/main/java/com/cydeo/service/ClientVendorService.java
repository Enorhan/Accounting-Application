package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import java.util.List;

public interface ClientVendorService {
    List<ClientVendorDto> listAllClientVendors();
    List<ClientVendorDto> listAllClientVendorsByCompany();
    ClientVendorDto findById(Long id);

    boolean existsByName(String clientVendorName);

    ClientVendorDto createClientVendor(ClientVendorDto clientVendorDto);
    void deleteClientVendor(Long id);
    ClientVendorDto updateClientVendor(Long id, ClientVendorDto clientVendorDTO);

}
