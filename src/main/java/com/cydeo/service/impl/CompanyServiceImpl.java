package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final SecurityService securityService;
    private final MapperUtil mapperUtil;


    public CompanyServiceImpl(CompanyRepository companyRepository, SecurityService securityService, MapperUtil mapperUtil, MapperUtil mapperUtil1) {
        this.companyRepository = companyRepository;
        this.securityService = securityService;
        this.mapperUtil = mapperUtil1;
    }


    @Override
    public CompanyDto findById(Long id) {
        Company company= companyRepository.findById(id)
                .orElseThrow( () ->new NoSuchElementException("Incorrect id" + id + " Try another Id"));
        return mapperUtil.convert(company,new CompanyDto());
    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(company -> mapperUtil.convert(company,new CompanyDto())).collect(Collectors.toList());
    }

    @Override
    public Long getCompanyIdByLoggedInUser() {
        return securityService.getLoggedInUser().getCompany().getId();
    }
}

