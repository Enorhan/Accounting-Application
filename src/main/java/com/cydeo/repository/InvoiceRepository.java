package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.entity.User;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    List<Invoice> findAllByInvoiceTypeOrderByInvoiceNoDesc(InvoiceType invoiceType);

     //finds the latest Sales invoice in DB
     @Query(value = "SELECT * FROM invoices WHERE invoice_no LIKE 'S-%' ORDER BY invoice_no DESC LIMIT 1", nativeQuery = true)
     Invoice findTopSalesInvoice();
}
