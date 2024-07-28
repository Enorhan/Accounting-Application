package com.cydeo.service;

import com.cydeo.dto.ClientVendorDto;
import java.util.List;
public interface ClientVendorService {

    ClientVendorDto createClientVendor(ClientVendorDto clientVendorDto);
    ClientVendorDto findById(Long id);
    void deleteClientVendor(Long id);
    ClientVendorDto updateClientVendor(Long id, ClientVendorDto clientVendorDTO);
    List<ClientVendorDto> findAll();

}
