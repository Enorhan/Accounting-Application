package com.cydeo.service;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;

import java.util.List;

public interface UserService {

    UserDto findByusername(String username);

    List<UserDto> listAllUser();


}
