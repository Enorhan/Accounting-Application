package com.cydeo.dto;

import com.cydeo.entity.Address;
import com.cydeo.enums.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyDto {

    private Long id;
    private String title;
    private String phone;
    private String website;
    private AddressDto address;
    private CompanyStatus companyStatus;
}
