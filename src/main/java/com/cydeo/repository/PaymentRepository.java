package com.cydeo.repository;

import com.cydeo.entity.Payment;
import com.cydeo.enums.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> findByYear(int year);
    Payment findByYearAndAndMonth(int year, Month month);


}
