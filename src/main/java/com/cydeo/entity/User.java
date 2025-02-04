package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Where(clause = "is_deleted = false")
public class User extends BaseEntity {
    private String firstname;
    private String lastname;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private boolean enabled;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;
    @ManyToOne
    @JoinColumn(name = "company_id",nullable = false)
    private Company company;
}
