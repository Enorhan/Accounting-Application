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
@Builder
public class UserDto {

    private Long id;
    @NotBlank(message = "Email is required field.")
    @Email(message = "Email is required field.")
    private String username;

    @NotBlank(message = "password is required field")
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,}")
    private String password;
    @NotNull(message = "Password should match")
    private String confirmPassword;

    @NotBlank(message = "First Name is required field.")
    @Size(max = 50, min = 2)
    private String firstname;
    @NotBlank(message = "Last Name is required field.")
    @Size(max = 50, min = 2)
    private String lastname;
    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^\\d{10}$")
    private String phone;
    @NotNull(message = "Please select a Role.")
    private RoleDto role;
    @NotNull(message = "Please select a Company.")
    private CompanyDto company;
    private Boolean isOnlyAdmin;
}
