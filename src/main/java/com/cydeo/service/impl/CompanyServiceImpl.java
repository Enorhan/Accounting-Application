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
    public Long getCompanyIdByLoggedInUser() {
        return securityService.getLoggedInUser().getCompany().getId();
    }

    @Override
    public CompanyDto getCompanyDtoByLoggedInUser() {
        return securityService.getLoggedInUser().getCompany();
    }

    @Override
    public String getCurrentCompanyTitle() {
        return securityService.getLoggedInUser().getCompany().getTitle();
    }

    @Override
    public CompanyDto findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Incorrect id" + id + " Try another Id"));
        return mapperUtil.convert(company, new CompanyDto());
    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .filter(company -> company.getId() != 1)
                .sorted((c1, c2) -> {
                    int status = c1.getCompanyStatus().compareTo(c2.getCompanyStatus());
                    if (status == 0) {
                        return c1.getTitle().compareTo(c2.getTitle());
                    }
                    return "Active".equals(c1.getCompanyStatus()) ? -1 : 1;
                })
                .map(company -> mapperUtil.convert(company, new CompanyDto())).collect(Collectors.toList());
    }
}

