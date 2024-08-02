package com.cydeo.repository;

import com.cydeo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByDescription(String description);

    Category findByDescriptionAndCompanyId(String description, Long companyId);
    List<Category>findAllByCompanyId(Long companyId);

}
