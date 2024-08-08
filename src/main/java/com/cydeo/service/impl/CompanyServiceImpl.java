package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.entity.common.UserPrincipal;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exceptions.CompanyNotFoundException;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final SecurityService securityService;
    private final MapperUtil mapperUtil;
    private final UserRepository userRepository;




    public CompanyServiceImpl(CompanyRepository companyRepository, SecurityService securityService, MapperUtil mapperUtil, MapperUtil mapperUtil1, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.securityService = securityService;
        this.mapperUtil = mapperUtil1;
        this.userRepository = userRepository;


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
    public CompanyDto save(CompanyDto companyDto) {
        Company company = mapperUtil.convert(companyDto, new Company());
        if (companyDto.getId() == null) {
            company.setCompanyStatus(CompanyStatus.PASSIVE);
            company.getAddress().setInsertDateTime(LocalDateTime.now());
        } else{
            if (company.getAddress().getInsertDateTime() == null) {
                company.getAddress().setInsertDateTime(LocalDateTime.now());
            }
        }
        return mapperUtil.convert(companyRepository.save(company), new CompanyDto());
    }

    @Override
    @Transactional
    public CompanyDto activateCompany(Long Id) {
        Company company = companyRepository.findById(Id).orElseThrow(() -> new CompanyNotFoundException("Company not found"));
         company.setCompanyStatus(CompanyStatus.ACTIVE);
         companyRepository.save(company);


        return mapperUtil.convert(company,new CompanyDto());


    }

    @Override
    @Transactional
    public CompanyDto deactivateCompany(Long Id) {
        Company company = companyRepository.findById(Id).orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        company.setCompanyStatus(CompanyStatus.PASSIVE);
        companyRepository.save(company);

     return mapperUtil.convert(company, new CompanyDto());

    }

    @Override
    public void updateCompany(CompanyDto companyDto) {
        Company existingCompany = companyRepository.findById(companyDto.getId()).orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        Company company = mapperUtil.convert(companyDto, existingCompany);
        company.setCompanyStatus(existingCompany.getCompanyStatus());
        companyRepository.save(company);
    }

    @Override
    public boolean existsByTitle(CompanyDto companyDto) {
        Company companyByTitle = companyRepository.findCompanyByTitle(companyDto.getTitle());
        if(companyByTitle == null){
            return false;
        }
        return !companyByTitle.getId().equals(companyDto.getId());

    }

    @Override
    public CompanyDto findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Incorrect id" + id + " Try another Id"));
        return mapperUtil.convert(company, new CompanyDto());
    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .filter(company -> company.getId() != 1)
                .map(company -> mapperUtil.convert(company, new CompanyDto()))
                .sorted(Comparator.comparing(CompanyDto::getCompanyStatus).thenComparing(CompanyDto::getTitle))
                .collect(Collectors.toList());
    }
}

