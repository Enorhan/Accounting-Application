package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.User;
import com.cydeo.repository.CompanyRepository;
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
import java.util.Objects;
import java.util.stream.Collectors;

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
        return mapperUtil.convert(user, new UserDto());
    }

    @Override
    public List<UserDto> listAllUser() {
        User currentUser = mapperUtil.convert(securityService.getLoggedInUser(), new User());
        List<User> userList;
        if (currentUser.getRole().getDescription().equals("Root User")) {//Root User can list only admins of all companies.
            userList = userRepository.findAllByRoleDescriptionAndIsDeleted("Admin",false);
        } else {
            userList = userRepository.findByCompanyIdAndIsDeleted(currentUser.getCompany().getId(),false);//Admin can only see his/her company's users.
        }

//        Users should be sorted by their companies then their roles.
        return userList.stream().sorted(Comparator.comparing((User user) -> user.getCompany().getTitle())
                        .thenComparing((User user) -> user.getRole().getDescription()))
                .map(entity -> {
                    UserDto dto = mapperUtil.convert(entity, new UserDto());
                    dto.setIsOnlyAdmin(dto.getRole().getDescription().equals("Admin") &&this.checkIfOnlyAdmin(dto));
                    return dto;
                })
                .toList();

    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        UserDto dto = mapperUtil.convert(user, new UserDto());
        dto.setIsOnlyAdmin(dto.getRole().getDescription().equals("Admin") && this.checkIfOnlyAdmin(dto));
        return dto;
    }

    @Override
    public boolean checkIfOnlyAdmin(UserDto userDto) {

        return userDto != null && Boolean.TRUE.equals(userDto.getIsOnlyAdmin());
    }

    @Override
    public boolean isOnlyAdmin(UserDto userDto) {
//        SELECT CASE WHEN COUNT(u) = 1 THEN TRUE ELSE FALSE END FROM User u WHERE u.role.description = 'Admin' AND u.company.id = :companyId")
        return userRepository.isOnlyAdmin(userDto.getCompany().getId());

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

        userRepository.save(user);
    }

    public List<CompanyDto> listCompaniesByLoggedInUser() {
        UserDto user = securityService.getLoggedInUser();
        if (user.getRole().getDescription().equals("Root User")) {
            return companyService.getAllCompanies().stream()
                    .filter(c -> !c.getTitle().equalsIgnoreCase("CYDEO"))
                    .collect(Collectors.toList());
        } else {
            return List.of(companyService.getCompanyDtoByLoggedInUser());
        }
    }

    @Override
    public boolean userNameExists(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());
        if (user == null) {
            return false;
        }
        return !Objects.equals(userDto.getId(), user.getId());
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        UserDto convertedDto = mapperUtil.convert(user, new UserDto());
        user.setIsDeleted(true);
        //save the object in the db
        userRepository.save(user);

    }


    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        return user.getId();
    }


    @Override
    public boolean isPasswordMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }


}
