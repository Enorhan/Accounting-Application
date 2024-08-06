package com.cydeo.service.unit_tests;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.repository.CompanyRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import com.cydeo.service.impl.UserServiceImpl;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)//te be able to mock some services and repositories all of these parties
class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MapperUtil mapperUtil;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Test
    void findByUserName_Test() {
        TestDocumentInitializer.getUser("Manager");

        // I'm calling the real method inside the main, which is the method I want to test.
        userServiceImpl.findByUsername("john@cydeo.com");

        // I'm checking if this method run or not.
        verify(userRepository).findByUsername("john@cydeo.com");

        verify(userRepository, times(1)).findByUsername("john@cydeo.com");

        verify(userRepository, atLeastOnce()).findByUsername("john@cydeo.com");
        verify(userRepository, atLeast(1)).findByUsername("john@cydeo.com");

        verify(userRepository, atMostOnce()).findByUsername("john@cydeo.com");
        verify(userRepository, atMost(10)).findByUsername("john@cydeo.com");

    }




}