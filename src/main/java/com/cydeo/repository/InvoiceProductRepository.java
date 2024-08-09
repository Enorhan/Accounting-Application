package com.cydeo.repository;

import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

    List<InvoiceProduct> findAllByOrderByInvoiceDesc();

    List<InvoiceProduct> findAllByInvoiceIdAndIsDeleted(Long id, boolean isDeleted);

    List<InvoiceProduct> findAllByInvoiceInvoiceStatusAndInvoiceInvoiceTypeAndInvoiceCompanyIdAndIsDeleted(
            InvoiceStatus invoice_invoiceStatus, InvoiceType invoice_invoiceType, Long invoice_company_id, Boolean isDeleted
    );

    List<InvoiceProduct> findAllByInvoiceCompanyIdAndInvoiceInvoiceStatus(Long companyId, InvoiceStatus invoiceStatus);

    @Query("SELECT ip FROM InvoiceProduct ip " +
            "WHERE ip.invoice.company.id = :companyId " +
            "AND ip.invoice.invoiceType = :invoiceType " +
            "AND ip.invoice.invoiceStatus = :invoiceStatus " +
            "AND ip.product.id = :productId " +
            "ORDER BY ip.invoice.date")
    List<InvoiceProduct> findPurchaseProducts(
            @Param("companyId") Long companyId,
            @Param("invoiceType") InvoiceType invoiceType,
            @Param("invoiceStatus") InvoiceStatus invoiceStatus,
            @Param("productId") Long productId);

    Boolean existsByProductId(Long productId);
}
