package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import com.cydeo.util.MapperUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto findByUserName(String username) {
        User user = userRepository.findByUsernameAndIsDeleted(username,false);
        return mapperUtil.convert(user,new UserDto());
    }

    @Override
    public List<UserDto> listAllUser() {

        List<User> userList = userRepository.findAllByIsDeletedOrderByCompanyTitleAsc(false);
        List<UserDto> userDtoList =userList.stream().map(user -> (
                mapperUtil.convert(user, new UserDto()))
        ).collect(Collectors.toList());
        return userDtoList;
    }



}
