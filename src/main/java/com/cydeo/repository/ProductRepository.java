package com.cydeo.repository;

import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query(value = "SELECT p.* FROM products p " +
            "JOIN categories c ON p.category_id = c.id " +
            "WHERE c.company_id = :companyId AND p.is_deleted = :isDeleted", nativeQuery = true)
    List<Product> findAllByCompanyIdAndIsDeleted(@Param("companyId") Long companyId, @Param("isDeleted") boolean isDeleted);

    Product findProductById(Long productId);
    Boolean existsByName(String productName);

}