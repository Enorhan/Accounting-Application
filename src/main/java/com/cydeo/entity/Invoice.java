package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice extends BaseEntity {

    String invoiceNo;
    InvoiceStatus invoiceStatus;
    InvoiceType invoiceType;
    LocalDate date;

    @ManyToOne
    ClientVendor clientVendor;

    @ManyToOne
    Company company;
}
