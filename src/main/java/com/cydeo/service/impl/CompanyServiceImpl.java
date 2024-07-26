package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final SecurityService securityService;

    public CompanyServiceImpl(SecurityService securityService) {
        this.securityService = securityService;
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
}

