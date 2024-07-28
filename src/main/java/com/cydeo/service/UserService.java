package com.cydeo.service;

import com.cydeo.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto findByUsername(String username);

    List<UserDto> listAllUser();
    UserDto findById(Long id);


    void save(UserDto user);

    UserDto update(UserDto user);

    Long getCurrentUserId();

}
