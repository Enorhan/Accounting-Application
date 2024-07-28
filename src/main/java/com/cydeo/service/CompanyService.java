package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;

public interface CompanyService {

     Long getCompanyIdByLoggedInUser();

     CompanyDto findById(Long id);

     List<CompanyDto> getAllCompanies();
}
