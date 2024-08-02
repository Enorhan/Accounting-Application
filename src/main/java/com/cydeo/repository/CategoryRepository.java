package com.cydeo.repository;

import com.cydeo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByDescription(String description);

    @Query("select e from #{#entityName} e where e.isDeleted = false")
    List<Category> findAll();

    Category findByDescriptionAndCompanyId(String description, Long companyId);
    List<Category>findAllByCompanyId(Long companyId);

}
