package com.cydeo.service.unit_tests;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.UserNotFoundException;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)//te be able to mock some services and repositories all of these parties
class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;
//    @Spy
//    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    @Mock
    private MapperUtil mapperUtil;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Test
    void findByUserName_Test() {
        UserDto userDto = TestDocumentInitializer.getUser("Manager");

        // I'm calling the real method inside the main, which is the method I want to test.
        userServiceImpl.findByUsername(userDto.getUsername());

        // I'm checking if this method run or not.
        verify(userRepository).findByUsername(userDto.getUsername());

        verify(userRepository, times(1)).findByUsername(userDto.getUsername());

        verify(userRepository, atLeastOnce()).findByUsername(userDto.getUsername());
        verify(userRepository, atLeast(1)).findByUsername(userDto.getUsername());

        verify(userRepository, atMostOnce()).findByUsername(userDto.getUsername());
        verify(userRepository, atMost(10)).findByUsername(userDto.getUsername());
    }
    @Test
    void findById_test() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
            Throwable throwable = catchThrowable(() ->
                    userServiceImpl.findById(1L));
            assertThat(throwable).hasMessage("User not found with id: " + 1L);
            assertThat(throwable).isInstanceOf(UserNotFoundException.class);
    }
    @Test
    void should_delete_whenUserExist(){
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setIsDeleted(false); // Initially, the user is not deleted
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userServiceImpl.delete(userId);
        assertTrue(user.getIsDeleted()); // Check that the product is marked as deleted
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }
    @Test
    void test_delete_whenUserNotExist(){
        Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(null);
    assertThrows(NullPointerException.class, () -> {
        userServiceImpl.delete(userId);
    });

    verify(userRepository).findById(userId);
    verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void test_onlyAdmin(){
        UserDto userDto=TestDocumentInitializer.getUser("Admin");
       when(userRepository.countAllByCompany_IdAndRole_Description(userDto.getCompany().getId(), "Admin")).thenReturn(1);
        boolean isOnlyAdmin = userServiceImpl.checkIfOnlyAdmin(userDto);
        assertTrue(isOnlyAdmin);
    }


}