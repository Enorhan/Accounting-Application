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
    @NotBlank()
    @Size(min = 2, max = 100)
    private String addressLine1;
    @Size(max = 100)
    private String addressLine2;
    @NotBlank()
    @Size(min = 2, max = 50)
    private String city;
    @NotBlank()
    @Size(min = 2, max = 50)
    private String state;
    @NotBlank()
    @Size(min = 2, max = 50)
    private String country;
    @NotBlank()
    @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$")
    private String zipCode;
}
