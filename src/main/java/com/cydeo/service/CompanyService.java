package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;

public interface CompanyService {

     CompanyDto getCompanyIdByLoggedInUser(Long id);
}
