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
    @NotBlank(message = "Title is a required field.")
    @Size(min = 2, max = 100,message = "Title should be 2-100 characters long.")
    private String title;
    @NotBlank(message = "Phone Number is a required field and may be in any valid phone number format.")
    @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$")
    private String phone;
    private String website;
    @Valid
    private AddressDto address;
    private CompanyStatus companyStatus;
}
