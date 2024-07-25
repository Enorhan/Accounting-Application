package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceProductRepository  extends JpaRepository<Invoice,Long> {
}
