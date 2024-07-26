package com.cydeo.repository;

import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceProductRepository  extends JpaRepository<InvoiceProduct,Long> {

    List<InvoiceProduct> findAllByInvoiceId(Long id);
    @Query(value = "select a.* from invoice_products a join invoices b on a.invoice_id=b.id where b.invoice_status='APPROVED' and b.company_id= :companyId order by b.date desc limit 3", nativeQuery = true)
    List<InvoiceProduct> find3LastApprovedTransactionDesc(@Param("companyId") Long companyId);
}
