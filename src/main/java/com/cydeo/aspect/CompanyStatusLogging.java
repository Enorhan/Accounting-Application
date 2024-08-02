package com.cydeo.aspect;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CompanyStatusLogging {

    Logger logger = LoggerFactory.getLogger(CompanyStatusLogging.class);

    private UserService userService;


    @Pointcut("execution(* com.cydeo.service.CompanyService.activateCompany(..))")
    public void activateMethod() {}

    @Pointcut("execution(* com.cydeo.service.CompanyService.deactivateCompany(..))")
    public void deactivateMethod() {}

    @AfterReturning("activateMethod()")
    public void logAfterActivation() {
        logCompanyStatusChange("activate");
    }

    @AfterReturning("deactivateMethod()")
    public void logAfterDeactivation() {
        logCompanyStatusChange("deactivate");
    }

    private void logCompanyStatusChange(String methodName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDto user = userService.findByUsername(username);


        logger.info(String.format("Method: %s, User: %s %s (%s)",
                methodName, user.getCompany().getTitle(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername()));
    }
}
