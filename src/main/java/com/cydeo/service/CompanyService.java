package com.cydeo.service;

import com.cydeo.dto.CompanyDto;

public interface CompanyService {

     Long getCompanyIdByLoggedInUser(Long id);

     CompanyDto findById(Long id);
}
