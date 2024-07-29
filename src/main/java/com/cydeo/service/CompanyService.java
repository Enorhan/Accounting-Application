package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;

public interface CompanyService {

     Long getCompanyIdByLoggedInUser();

     CompanyDto findById(Long id);

     List<CompanyDto> getAllCompanies();

     CompanyDto getCompanyDtoByLoggedInUser();

     String getCurrentCompanyTitle();

     CompanyDto save(CompanyDto companyDto);

     CompanyDto activateCompany(Long Id);
     CompanyDto deactivateCompany(Long Id);
     void updateCompany(CompanyDto companyDto);

}
