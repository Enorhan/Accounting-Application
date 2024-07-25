package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientVendorDto {

    @NotNull
    private Long id;
    private String clientVendorName;
    private String phone;
    private String website;
    private ClientVendorType clientVendorType;
    private AddressDto address;
    private CompanyDto company;

    private boolean hasInvoice;
}

