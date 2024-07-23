package com.cydeo.service;

import com.cydeo.dto.UserDto;

public interface UserService {
    UserDto findByUserName(String username);
}
