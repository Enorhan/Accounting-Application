package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import com.cydeo.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, @Lazy CompanyService companyService, @Lazy PasswordEncoder passwordEncoder, @Lazy SecurityService securityService) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return mapperUtil.convert(user,new UserDto());
    }

    @Override
    public List<UserDto> listAllUser() {
        User currentUser = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        List<User> userList;
        if (currentUser.getRole().getDescription().equals("Root User")) {//Root User can list only admins of all companies.
            userList = userRepository.findAllByRoleDescription("Admin");
        } else {
            userList = userRepository.findByCompanyId(currentUser.getCompany().getId());//Admin can only see his/her company's users.
        }

//        Users should be sorted by their companies then their roles.
        return userList.stream().sorted(Comparator.comparing((User user)-> user.getCompany().getTitle())
                        .thenComparing((User user)-> user.getRole().getDescription()))
                .map(entity -> {
                    UserDto dto = mapperUtil.convert(entity, new UserDto());
                    dto.setIsOnlyAdmin(dto.getRole().getDescription().equals("Admin") && this.checkIfOnlyAdmin(dto));
                    return dto;
                })
                .toList();

    }
    @Override
    public UserDto findById(Long id){
        User user = userRepository.findById(id).orElseThrow(()->new NoSuchElementException("User not found with id: " + id));
        UserDto dto = mapperUtil.convert(user, new UserDto());
        dto.setIsOnlyAdmin(dto.getRole().getDescription().equals("Admin") && this.checkIfOnlyAdmin(dto));
        return dto;
    }
    private boolean checkIfOnlyAdmin(UserDto userDto) {

        if (userDto != null && Boolean.TRUE.equals(userDto.getIsOnlyAdmin())) {
            return true;
        }
        return false;
    }

    @Override

    public void save(UserDto userDto) {
        User user = mapperUtil.convert(userDto, new User());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
    }



    @Override
    public void update(UserDto userDto) {
        User user = mapperUtil.convert(userDto, new User());
        UserDto loggedInUserDto = securityService.getLoggedInUser();
        User user1 = mapperUtil.convert(loggedInUserDto, new User());
        user.setId(user1.getId());

        userRepository.save(user1);
    }

    @Override
    public void delete(Long id) {
        //go to db and get that user with useName
        User user = userRepository.findById(id).orElseThrow(()->new NoSuchElementException("User not found with id: " + id));
        //change the isDeleted field to true
        user.setIsDeleted(true);
        //save the object in the db
        userRepository.save(user);
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        return mapperUtil.convert(user,new UserDto());

    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        return user.getId();
    }


}
