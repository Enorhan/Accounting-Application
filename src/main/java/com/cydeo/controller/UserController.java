package com.cydeo.controller;

import com.cydeo.dto.UserDto;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/list")
    public String retrieveUserList(Model model) {
       model.addAttribute("users", userService.listAllUser());
        return "/user/user-list";
    }
}
