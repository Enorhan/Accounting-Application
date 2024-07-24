package com.cydeo.service;

import com.cydeo.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto findByUserName(String username);

    List<UserDto> listAllUser();

}
