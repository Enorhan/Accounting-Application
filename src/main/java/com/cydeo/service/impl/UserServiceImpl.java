package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import com.cydeo.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;

    private final SecurityService securityService;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil, @Lazy PasswordEncoder passwordEncoder, @Lazy CompanyService companyService, @Lazy SecurityService securityService) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
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


        userRepository.save(mapperUtil.convert(userDto,new User()));
    }

    private Map<String, Object> getOptions() {
       // If current user is a "Root User", selectable option will be only "Admin" and it should be selected by default
//If current user is an "Admin", selectable options will be "Admin", "Manager", and "Employee"
        User currentUser = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        List<String> options;
        String defaultSelection;

        switch (currentUser.getRole().getDescription()) {
            case "Root User":
                options = List.of("Admin");
                defaultSelection = "Admin";
                break;
            case "Admin":
                options = List.of("Admin", "Manager", "Employee");
                defaultSelection = "Admin";
                break;
            default:
                options = List.of(); // No options available
                defaultSelection = "";
                break;
        }
        return Map.of("options", options, "defaultSelection", defaultSelection);
    }

    @Override
    public void update(UserDto userDto) {

        User user = mapperUtil.convert(userDto, new User());
        userRepository.save(user);
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


}
