package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
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
        UserDto currentUser = userService.getCurrentUser();
        String currentUserRole = currentUser.getRole().getDescription(); // Implement this method to get the current user role
        List<String> roles = currentUserRole.equals("Root User")
                ? List.of("Admin")
                : List.of("Admin", "Manager", "Employee");

        List<CompanyDto> companies = currentUserRole.equals("Root User")
                ? companyService.getAllCompaniesExcept("CYDEO")
                : List.of(companyService.getCompanyByUserId(userService.getCurrentUserId()));

        model.addAttribute("newUser", new UserDto());
        model.addAttribute("user", currentUser);
        model.addAttribute("roles", roles);
        model.addAttribute("companies", companies);
        model.addAttribute("currentUserRole", currentUserRole);


        model.addAttribute("companies", companyService.getAllCompanies());
        return "/user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@ModelAttribute("newUser") UserDto userDto){

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
        return "/user/user-update";

    }


    @PutMapping("/update")
    public String updateUser( @ModelAttribute("newUser") UserDto userDto) {

        userService.update(userDto);

        return "redirect:/users/list";

    }

    @ModelAttribute
    public void commonAttributes(Model model) {

        model.addAttribute("users", userService.listAllUser());
        model.addAttribute("Title","Cydeo Accounting-User");
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.getAllCompanies());

    }

//    @GetMapping("/delete/{id}")
//    public String deleteUser(@PathVariable("id") Long id) {
//        userService.delete(id);
//
//        return "redirect:/user/create";
//    }

}
