package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;
    private final SecurityService securityService;

    public UserController(UserService userService, RoleService roleService, @Lazy CompanyService companyService, @Lazy SecurityService securityService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
        this.securityService = securityService;
    }

    @GetMapping("/list")
    public String retrieveUserList(Model model) {
        model.addAttribute("users", userService.listAllUser());
        return "/user/user-list";
    }


    @GetMapping("/create")
    public String createUserForm(Model model) {
//
        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listRolesByLoggedInUser());
        model.addAttribute("companies", userService.listCompaniesByLoggedInUser());

        return "/user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@Valid @ModelAttribute("newUser") UserDto userDto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("userRoles", roleService.listRolesByLoggedInUser());
            model.addAttribute("companies", userService.listCompaniesByLoggedInUser());

            return "/user/user-create";
        }
        if (userService.userNameExists(userDto.getUsername())) {
            result.rejectValue("username",
                    "A user with this email already exists. Please try with different email.");
        }

        userService.save(userDto);
        return "redirect:/users/list";
    }

    @PostMapping("/reset")
    public String resetForm() {
        // Redirect to the form page to reset the fields

        return "redirect:/users/create";
    }


    @GetMapping("/update/{id}")
    public String editUser(@PathVariable("id") Long id, @ModelAttribute UserDto user, Model model) {

        UserDto userDto = userService.findById(id);
        model.addAttribute("user", userDto);
        model.addAttribute("users", userService.listAllUser());
        model.addAttribute("userRoles", roleService.listRolesByLoggedInUser());
        model.addAttribute("companies", userService.listCompaniesByLoggedInUser());

        return "/user/user-update";

    }


    @PostMapping("/update/{id}")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto,BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("users", userService.listAllUser());
            model.addAttribute("userRoles", roleService.listRolesByLoggedInUser());
            model.addAttribute("companies", userService.listCompaniesByLoggedInUser());

            return "/user/user-update";
        }
        if (userService.userNameExists(userDto.getUsername())) {
            result.rejectValue("username",
                    "A user with this email already exists. Please try with different email.");
        }
        if (userService.isPasswordNotMatch(userDto.getPassword())){
            result.rejectValue("password","Passwords should match.");
        }

//        model.addAttribute("users", userService.listAllUser());
//        model.addAttribute("userRoles", roleService.listRolesByLoggedInUser());
//        model.addAttribute("companies", userService.listCompaniesByLoggedInUser());

        userService.update(userDto);

        return "redirect:/users/list";
    }



    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        UserDto userDto = userService.findById(id);

        if (userService.checkIfOnlyAdmin(userDto)){
            model.addAttribute("error", "Cannot delete the only admin of the company.");
            return "redirect:/users/list";
        }
        userService.delete(id);

        return "redirect:/users/list";
    }

}
