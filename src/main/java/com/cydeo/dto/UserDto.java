package com.cydeo.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class UserDto {

    private Long id;
    @NotBlank(message = "Email is required field.")
    @Email(message = "A user with this email already exists. Please try with different email.")
    private String username;

    @NotBlank(message = "password is required field")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W\\d]).{4,}$",
            message = "Password should be at least 4 characters long and needs to contain 1 capital letter, 1 small letter and 1 special character or number.")
    private String password;
    @NotNull(message = "Password should match")
    private String confirmPassword;

    @NotBlank(message = "First Name is required field.")
    @Size(max = 50, min = 2)
    private String firstname;
    @NotBlank(message = "Last Name is required field.")
    @Size(max = 50, min = 2)
    private String lastname;
    @NotBlank(message = "Phone Number is a required field.")
    @Pattern(regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$",
            message = "Phone Number is a required field and may be in any valid phone number format.")
    private String phone;
    @NotNull(message = "Please select a Role.")
    private RoleDto role;
    @NotNull(message = "Please select a Company.")
    private CompanyDto company;

    private Boolean isOnlyAdmin;
}
