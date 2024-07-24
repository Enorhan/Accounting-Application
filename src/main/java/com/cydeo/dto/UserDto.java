package com.cydeo.dto;


import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String confirmPassword;
    private boolean enabled;
    private String phone;
    private RoleDto role;
    private CompanyDto company;
    private boolean isOnlyAdmin;

}
