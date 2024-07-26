package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

public interface CompanyService {

     Long getCompanyIdByLoggedInUser();

     CompanyDto getCompanyDtoByLoggedInUser();

     String getCurrentCompanyTitle();
}
