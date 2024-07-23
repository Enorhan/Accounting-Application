package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Category extends BaseEntity {
    @Column(nullable = false)
    public String description;

    @ManyToOne
    public Company company;
}
