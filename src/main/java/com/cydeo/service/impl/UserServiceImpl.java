package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import com.cydeo.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, @Lazy SecurityService securityService) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return mapperUtil.convert(user,new UserDto());
    }

    @Override
    public List<UserDto> listAllUser() {
        List<User> useList = userRepository.findAll(Sort.by("firstname"));
        List<UserDto> userDtoList = useList.stream().map(user -> (
                mapperUtil.convert(user, new UserDto()))
        ).collect(Collectors.toList());
        return userDtoList;
    }

    @Override
    public Long getCurrentUserId() {
        return securityService.getLoggedInUser().getId();
    }
}
