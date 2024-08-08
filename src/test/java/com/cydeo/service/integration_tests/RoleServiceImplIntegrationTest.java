package com.cydeo.service.integration_tests;

import com.cydeo.dto.ProductDto;
import com.cydeo.dto.RoleDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Role;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.SecuritySetUpUtil;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleServiceImplIntegrationTest {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private MapperUtil mapperUtil;
    @BeforeEach
    void setUp() {
        SecuritySetUpUtil.setUpSecurityContext();
    }

    @Test
    void should_getAllRoles(){
        List<RoleDto> roleDtos = roleService.listRolesByLoggedInUser();
        assertNotNull(roleDtos);
        assertEquals(3, roleDtos.size());
    }
    @Test
    void test_findById(){
        UserDto loggedInUser = securityService.getLoggedInUser();

        Throwable throwable = catchThrowable(() -> roleService.findById(400L));
        assertThat(throwable).isInstanceOf(NoSuchElementException.class);
        assertThat(throwable).hasMessage("role not found with id: " + 400L);

        RoleDto roleDto = roleService.findById(loggedInUser.getId());
        assertThat(roleDto.getDescription()).isEqualTo("Employee");
        assertNotNull(roleDto);
    }

    @Test
    void should_save_role(){
        UserDto loggedInUser = securityService.getLoggedInUser();
        String description = loggedInUser.getRole().getDescription();
        RoleDto roleDto = loggedInUser.getRole();
        Role convertedRole = mapperUtil.convert(roleDto, new Role());
        roleRepository.save(convertedRole);
        assertThat(description).isEqualTo(convertedRole.getDescription());

    }


}
