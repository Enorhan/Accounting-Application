package com.cydeo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    private Long id;
    @NotBlank(message = "Address should have 2-100 characters long.")
    @Size(min = 2, max = 100)
    private String addressLine1;
    @Size(max = 100, message = "Address should have maximum 100 characters long.")
    private String addressLine2;
    @NotBlank(message = "City should have 2-50 characters long.")
    @Size(min = 2, max = 50)
    private String city;
    @NotBlank(message = "State should have 2-50 characters long.")
    @Size(min = 2, max = 50)
    private String state;
    @NotBlank(message = "Country should have 2-50 characters long.")
    @Size(min = 2, max = 50)
    private String country;
    @NotBlank(message = "Zipcode should have a valid form.")
    @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$")
    private String zipCode;
}
