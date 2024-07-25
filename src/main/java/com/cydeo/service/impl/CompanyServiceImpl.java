package com.cydeo.service.impl;

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
    public Long getCompanyIdByLoggedInUser(Long id) {
        return securityService.getLoggedInUser().getCompany().getId();
    }
}

