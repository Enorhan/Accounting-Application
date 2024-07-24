package com.cydeo.service.impl;


import com.cydeo.dto.ClientVendorDto;
import com.cydeo.entity.ClientVendor;
import com.cydeo.repository.ClientVendorRepository;
import com.cydeo.service.ClientVendorService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepository clientVendorRepository;
    private final MapperUtil mapperUtil;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, MapperUtil mapperUtil){

        this.clientVendorRepository = clientVendorRepository;
        this.mapperUtil = mapperUtil;

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

}
