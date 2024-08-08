package com.cydeo.service.integration_tests;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.User;
import com.cydeo.exception.UserNotFoundException;
import com.cydeo.service.*;
import com.cydeo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        SecuritySetUpUtil.setUpSecurityContext();
    }

    @Test
    void should_get_allUsers_byCompanyId (){
        Long companyId = securityService.getLoggedInUser().getId();
        List<User> userList = userRepository.findByCompanyId(companyId);
        assertThat(userList.size()).isEqualTo(1);
    }
    @Test
    void should_get_allUsers_by_byRoleDescription(){
        UserDto loggedInUser = securityService.getLoggedInUser();
        List<User> userList = userRepository.findAllByRoleDescription(loggedInUser.getRole().getDescription());
        assertThat(userList.size()).isEqualTo(2);
    }

    @Test
    void should_findById() {
        Throwable throwable = catchThrowable(() -> userService.findById(20L));
        assertThat(throwable).isInstanceOf(UserNotFoundException.class);
        assertThat(throwable).hasMessage("User not found with id: " + 20L);
        UserDto userDto = userService.findById(2L);
        assertThat(userDto.getUsername()).isEqualTo("admin@greentech.com");
        assertNotNull(userDto);
    }

    @Test
    void should_count_Admin() {
        UserDto loggedInUser = securityService.getLoggedInUser();
        Long companyId = loggedInUser.getCompany().getId();
        Integer admin = userRepository.countAllByCompany_IdAndRole_Description(companyId, "Admin");
        assertThat(admin).isEqualTo(2);
    }

    @Test
    void should_save_user() {
        UserDto userDto = TestDocumentInitializer.getUser("Admin");
        userService.save(userDto);
        User user = userRepository.findByUsername(userDto.getUsername());
        assertThat(user).isNotNull();
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(1L, userDto.getId());
    }

    @Test
    void should_update_user() {
        UserDto userDto = TestDocumentInitializer.getUser("Admin");
        userDto.setUsername("employee@greentech.com");
        userService.update(userDto);
        User userById = userRepository.findById(userDto.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        assertThat(userById).isNotNull();
        assertEquals("employee@greentech.com", userDto.getUsername());
        assertEquals(1L, userDto.getId());

    }

    @Test
    void should_delete_user() {
        User user = userRepository.findById(1L).orElseThrow();
        userService.delete(user.getId());
        assertEquals(true, user.getIsDeleted());
    }

    @Test
    void should_user_passwordIsMatch() {
        UserDto loggedInUser = securityService.getLoggedInUser();
        loggedInUser.setPassword("Abc1");
        loggedInUser.setConfirmPassword("Abc1");
        boolean passwordMatch = userService.isPasswordMatch(loggedInUser.getPassword(), loggedInUser.getConfirmPassword());
        assertTrue(passwordMatch);
    }

    @Test
    void should_check_userNameExist_whenExist() {
        UserDto loggedInUser = securityService.getLoggedInUser();
        assertThat(loggedInUser.getUsername()).isEqualTo("manager@greentech.com");
        UserDto userDto = TestDocumentInitializer.getUser("manager");
        userDto.setUsername("manager@greentech.com");
        boolean b = userService.userNameExists(userDto);
        assertTrue(b, "userName is Exist");
    }

    @Test
    void should_check_userNameExist_whenNotExist() {
        UserDto loggedInUser = securityService.getLoggedInUser();
        loggedInUser.setUsername("admin@redTech.com");
        boolean b = userService.userNameExists(loggedInUser);
        assertFalse(b, "userName is not Exist");
    }

    @Test
    void should_list_Companies_except_ByLoggedInUser() {
        List<CompanyDto> companyLists = userService.listCompaniesByLoggedInUser();
        CompanyDto companyDtoByLoggedInUser = companyService.getCompanyDtoByLoggedInUser();
        assertThat(companyLists.get(0).getTitle()).isEqualTo(companyDtoByLoggedInUser.getTitle());
        assertThat(companyLists.get(0).getTitle()).isNotEqualTo("CYDEO");

    }


}
