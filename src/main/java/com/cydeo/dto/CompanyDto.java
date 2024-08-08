package com.cydeo.dto;

import com.cydeo.entity.Address;
import com.cydeo.enums.CompanyStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CompanyDto {

    private Long id;
    @NotBlank()
    @Size(min = 2, max = 100)
    private String title;
    @NotBlank()
    @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$")
    private String phone;
    private String website;
    @Valid
    private AddressDto address;
    private CompanyStatus companyStatus;
}
