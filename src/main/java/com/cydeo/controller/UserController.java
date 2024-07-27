package com.cydeo.controller;

import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;

    public UserController(UserService userService, RoleService roleService, @Lazy CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }
    @GetMapping("/list")
    public String retrieveUserList(Model model) {
       model.addAttribute("users", userService.listAllUser());
        return "/user/user-list";
    }


    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("newUser", new UserDto());

        model.addAttribute("Title","Cydeo Accounting-User");
        model.addAttribute("users", userService.listAllUser());
        model.addAttribute("userRoles", roleService.listAllRoles());
        List<RoleDto> roleDtos = roleService.listAllRoles();
        List<String>


        model.addAttribute("companies", companyService.getAllCompanies());
        return "/user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@ModelAttribute("newUser") UserDto userDto,BindingResult result,Model model) {

        if (result.hasErrors()) {
            model.addAttribute("Title","Cydeo Accounting-User");
            model.addAttribute("users", userService.listAllUser());
            model.addAttribute("userRoles", roleService.listAllRoles());
            model.addAttribute("companies", companyService.getAllCompanies());

            return "/user/user-create";

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
    public String editUser(@PathVariable("id") Long id, Model model) {

        UserDto userDto = userService.findById(id);
        model.addAttribute("user", userDto);
        model.addAttribute("users", userService.listAllUser());
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.getAllCompanies());

        return "/user/user-update";

    }


    @PostMapping("/update")
    public String updateUser( @ModelAttribute("newUser") UserDto user) {

        userService.update(user);

        return "redirect:/users/list";

    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);

        return "redirect:/user/create";
    }

}
