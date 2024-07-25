package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice extends BaseEntity {

//    @Column(unique = true, nullable = false)
    String invoiceNo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    InvoiceStatus invoiceStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    InvoiceType invoiceType;

    @Column(nullable = false)
    LocalDate date;

    @ManyToOne
    ClientVendor clientVendor;

    @ManyToOne
    Company company;
}
