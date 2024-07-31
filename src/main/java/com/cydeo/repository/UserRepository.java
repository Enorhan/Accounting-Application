package com.cydeo.repository;

import com.cydeo.entity.Company;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);

    //Root User can list only admins of all companies.
    //Admin can only see his/her company's users.
    List<User> findAllByRoleDescriptionAndIsDeleted(String roleDescription,boolean isDeleted);
    List<User> findByCompanyIdAndIsDeleted(Long companyId,boolean isDeleted);

    @Query("SELECT CASE WHEN COUNT(u) = 1 THEN TRUE ELSE FALSE END FROM User u WHERE u.role.description = 'Admin' AND u.company.id = :companyId")
    boolean isOnlyAdmin(@Param("companyId") Long companyId);
//    boolean isOnlyAdminInCompany(Long id);


}
