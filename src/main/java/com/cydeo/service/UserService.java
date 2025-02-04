package com.cydeo.service;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto findByUsername(String username);

    List<UserDto> listAllUser();
    UserDto findById(Long id);


    void save(UserDto user);

    UserDto update(UserDto user);
    void delete(Long id);
    List<CompanyDto> listCompaniesByLoggedInUser();
    boolean userNameExists(UserDto userDto);
    boolean isPasswordMatch(String password,String confirmPassword);
   boolean checkIfOnlyAdmin(UserDto userDto);




}
