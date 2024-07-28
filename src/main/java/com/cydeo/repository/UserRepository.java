package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);

    //Root User can list only admins of all companies.
    //Admin can only see his/her company's users.
    List<User> findAllByRoleDescription(String roleDescription);
    List<User> findByCompanyId(Long companyId);
    boolean existsByUsername(String userName);

}
