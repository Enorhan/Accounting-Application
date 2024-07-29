package com.cydeo.service.impl;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.service.CompanyService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final InvoiceRepository invoiceRepository;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil, CompanyService companyService, InvoiceRepository invoiceRepository) {
        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<ClientVendorDto> listAllClientVendors() {
        List<ClientVendor> clientVendorList = clientVendorRepository.findAll();
        return clientVendorList.stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> listAllClientVendorsByCompany() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();
        List<ClientVendor> clientVendors = clientVendorRepository.findAllByCompanyIdOrderByTypeAndName(companyId);
        return clientVendors.stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public ClientVendorDto createClientVendor(ClientVendorDto clientVendorDTO) {
        clientVendorDTO.setCompany(companyService.getCompanyDtoByLoggedInUser());
        ClientVendor clientVendor = mapperUtil.convert(clientVendorDTO, new ClientVendor());
        clientVendor = clientVendorRepository.save(clientVendor);
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }

    @Override
    public ClientVendorDto findById(Long id) {
        ClientVendor clientVendor = clientVendorRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("ClientVendor not found"));
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> findAll() {
        List<ClientVendor> clientVendorList = clientVendorRepository.findAll();
        return clientVendorList.stream()
        .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
        .collect(Collectors.toList());
    }

    @Override
    public ClientVendorDto updateClientVendor(Long id, ClientVendorDto clientVendorDTO) {
        ClientVendor clientVendor = clientVendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClientVendor not found"));
        ClientVendor updatedClientVendor = mapperUtil.convert(clientVendorDTO, clientVendor);
        updatedClientVendor.setCompany(clientVendor.getCompany());
        clientVendor = clientVendorRepository.save(updatedClientVendor);
        return mapperUtil.convert(clientVendor, new ClientVendorDto());
    }

    @Override
    public void deleteClientVendor(Long id) {
        boolean hasInvoices = invoiceRepository.existsByClientVendorId(id);

        if (hasInvoices) {
            throw new IllegalStateException("Can not be deleted! This client/vendor has invoice(s).");
        }

        clientVendorRepository.deleteById(id);
    }
}
