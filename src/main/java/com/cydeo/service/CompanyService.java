package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;

public interface CompanyService {
     CompanyDto findById(Long id);

     List<CompanyDto> getAllCompanies();

     Long getCompanyIdByLoggedInUser();

     CompanyDto getCompanyDtoByLoggedInUser();

     String getCurrentCompanyTitle();

     CompanyDto save(CompanyDto companyDto);
}
