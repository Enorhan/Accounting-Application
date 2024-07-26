package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.entity.User;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    List<Invoice> findAllByInvoiceTypeAndCompanyIdOrderByInvoiceNoDesc(InvoiceType invoiceType, Long companyId);

     //finds the latest Sales invoice in DB
     @Query(value = "SELECT i.* FROM invoices i JOIN companies c ON i.company_id = c.id WHERE i.invoice_no LIKE 'S-%' AND c.title = :companyTitle ORDER BY i.invoice_no DESC LIMIT 1", nativeQuery = true)
     Invoice findTopSalesInvoice(@Param("companyTitle") String companyTitle);

}
