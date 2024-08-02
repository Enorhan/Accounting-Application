package com.cydeo.dto;

import com.cydeo.enums.ClientVendorType;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientVendorDto {

//  @NotNull
    private Long id;

    @NotBlank(message = "Client/Vendor Name is a required field.")
    @Size(min = 2, max = 50, message = "Client/Vendor Name must be between 2 and 50 characters long.")
    private String clientVendorName;

    @NotBlank(message = "Phone Number is required field.")
    @Pattern(regexp = "^\\+\\d{1,3} \\(\\d{3}\\) \\d{3}-\\d{4}$", message = "Phone Number may be in any valid phone number format(+1 (123) 4567890.")
    private String phone;

    @Pattern(regexp = "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}(/.*)?$",
            message = "Website should have a valid format.")
    private String website;

    @NotNull(message = "Please select type.")
    private ClientVendorType clientVendorType;

    @NotNull(message = "Address is required field.")
    @Valid
    private AddressDto address;

    @Valid
    private CompanyDto company;

    private boolean hasInvoice;
}

