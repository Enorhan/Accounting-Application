package com.cydeo.controller;

import com.cydeo.dto.UserDto;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping("/list")
    public String retrieveUserList(Model model) {
       model.addAttribute("users", userService.listAllUser());
        return "/user/user-list";
    }

//    @PostMapping("/create")
//    public String insertUser(@ModelAttribute("newUser") UserDto user, Model model) {
//        model.addAttribute("newUser", new UserDto());
////        model.addAttribute("roles", roleService.listAllRoles());
//        return "/user/user-create";
//    }
//    @PostMapping("/create")
//    public String saveUser(@ModelAttribute("newUser") UserDto user, Model model) {
//        userService.save(user);
//        return "redirect:/user/list";
//    }
//
//    @PostMapping("/update")
//    public String updateUser( @ModelAttribute("newUer") UserDto user) {
//
//        userService.update(user);
//
//        return "redirect:/user/user-create";
//
//    }
}
