package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

import java.util.List;

public interface CompanyService {

     Long getCompanyIdByLoggedInUser(Long id);

     CompanyDto findById(Long id);

     List<CompanyDto> getAllCompanies();
}
