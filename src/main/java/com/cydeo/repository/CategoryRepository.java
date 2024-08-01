package com.cydeo.repository;

import com.cydeo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByDescription(String description);

    @Query("select e from #{#entityName} e where e.company.id = :companyId and e.isDeleted = false order by e.description asc")
    List<Category> findAllByCompanyId(@Param("companyId") Long companyId);

    Category findByDescriptionAndCompanyId(String description, Long companyId);
}
