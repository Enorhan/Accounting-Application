package com.cydeo.service.impl;

import com.cydeo.entity.Address;
import com.cydeo.repository.AddressRepository;
import com.cydeo.service.AddressService;

import java.time.LocalDateTime;

public class AddressServiceImpl implements AddressService {

    AddressRepository addressRepository;

    @Override
    public void saveOrUpdateAddress(Address address) {
        Address address1 = new Address();
        // Set fields from addressDto to address
        address1.setInsertDateTime(LocalDateTime.now());
        addressRepository.save(address);
    }
}
