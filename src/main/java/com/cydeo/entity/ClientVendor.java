package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.ClientVendorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clients_vendors")
public class ClientVendor extends BaseEntity {

    @Column(nullable = false)
    private String clientVendorName;

    @Column(nullable = false)
    private String phone;

    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientVendorType clientVendorType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
