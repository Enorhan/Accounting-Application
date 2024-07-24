package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;

    


    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil) {
        this.companyRepository = companyRepository;

        this.mapperUtil = mapperUtil;
    }


    @Override
    public CompanyDto getCompanyIdByLoggedInUser(Long id) {
        Company companyDto = companyRepository.findByUserId(id);
        return mapperUtil.convert(companyDto,new CompanyDto());
    }
}

