package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);
    List<User> findAllByIsDeletedOrderByCompanyTitleAsc(Boolean deleted);




}
