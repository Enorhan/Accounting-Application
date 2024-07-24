package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity
public class Role extends BaseEntity {

    private String description;


}
