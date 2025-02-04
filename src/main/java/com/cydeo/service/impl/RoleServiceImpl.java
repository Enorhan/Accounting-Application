package com.cydeo.service.impl;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.exceptions.RoleNotFoundException;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final MapperUtil mapperUtil;
    private final SecurityService securityService;

    public RoleServiceImpl(RoleRepository roleRepository, MapperUtil mapperUtil, SecurityService securityService) {
        this.roleRepository = roleRepository;
        this.mapperUtil = mapperUtil;
        this.securityService = securityService;
    }

    @Override
    public RoleDto findById(Long id) throws RoleNotFoundException{
        Role role = roleRepository.findById(id).orElseThrow(()->new RoleNotFoundException("role not found with id: "+id));
        return mapperUtil.convert(role,new RoleDto());
    }
    @Override
    public List<RoleDto> listRolesByLoggedInUser(){
        UserDto user = securityService.getLoggedInUser();
        if (user.getRole().getDescription().equals("Root User")) {
            RoleDto admin = mapperUtil.convert(roleRepository.findByDescription("Admin"), new RoleDto());
            return List.of(admin);
        } else {
            return roleRepository.findAll()
                    .stream()
                    .filter(role -> !role.getDescription().equals("Root User"))
                    .map(role -> mapperUtil.convert(role, new RoleDto()))
                    .toList();
        }
    }
}
