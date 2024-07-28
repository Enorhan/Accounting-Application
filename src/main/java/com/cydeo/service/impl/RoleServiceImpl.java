package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import com.cydeo.util.MapperUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;
    private final UserService userService;

    public RoleServiceImpl(RoleRepository roleRepository, MapperUtil mapperUtil, UserService userService) {
        this.roleRepository = roleRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
    }

    @Override
    public RoleDto findById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(()->new NoSuchElementException("role not found with id: "+id));
        return mapperUtil.convert(role,new RoleDto());
    }
    @Override
    public List<RoleDto> listAllRoles() {
        UserDto currentUser = userService.getCurrentUser();
        RoleDto roleDto = currentUser.getRole();


        return roleRepository.findAll().stream()
                .map(role -> mapperUtil.convert(role, new RoleDto())).toList();
    }
}
