package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoice_products")
public class InvoiceProduct extends BaseEntity {

    int quantity;
    BigDecimal price;
    int tax;
    BigDecimal profitLoss;
    int remainingQuantity;

    @ManyToOne
    Invoice invoice;

    @ManyToOne
    Product product;
}