package com.cydeo.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {

    private Long id;
    private String username;
    private String password;
    private String confirmPassword;
    private String firstname;
    private String lastname;
    private String phone;
    private RoleDto role;
    private CompanyDto company;
    private Boolean isOnlyAdmin;

    public UserDto(Long id, String username,
                   String password, String confirmPassword,
                   String firstname, String lastname, String phone,
                   RoleDto role, CompanyDto company) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.role = role;
        this.company = company;
        this.isOnlyAdmin = role.getDescription().equalsIgnoreCase("Admin");
    }
}
