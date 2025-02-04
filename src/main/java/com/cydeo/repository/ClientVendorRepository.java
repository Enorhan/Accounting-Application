package com.cydeo.repository;

import com.cydeo.entity.ClientVendor;
import com.cydeo.enums.ClientVendorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientVendorRepository extends JpaRepository<ClientVendor, Long> {

    @Query("SELECT cv FROM ClientVendor cv WHERE cv.company.id = :companyId ORDER BY cv.clientVendorType ASC, cv.clientVendorName ASC")
    List<ClientVendor> findAllByCompanyIdOrderByTypeAndName(@Param("companyId") Long companyId);
    List<ClientVendor> findAllByCompanyIdAndClientVendorType(Long companyId, ClientVendorType clientVendorType);
    boolean existsByClientVendorName(String clientVendorName);
    List<ClientVendor>findAllByCompanyIdAndClientVendorTypeAndIsDeleted(Long companyId, ClientVendorType clientVendorType,Boolean isDeleted);
}
