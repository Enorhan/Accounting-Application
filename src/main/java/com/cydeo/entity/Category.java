package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends BaseEntity {
    @Column(nullable = false)
    private String description;

    @ManyToOne
    private Company company;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private List<Product> productList;
}