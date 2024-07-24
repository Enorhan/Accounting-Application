package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    List<Invoice> findAllByOrderByInvoiceNoDesc();

}
