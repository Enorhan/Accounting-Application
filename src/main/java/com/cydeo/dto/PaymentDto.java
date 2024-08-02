package com.cydeo.dto;

import com.cydeo.enums.Month;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PaymentDto {
    private Long id;
    private Integer year;
    private Month month;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private boolean isPaid;
    private String companyStripeId;
    private String description;

    private CompanyDto company;
}
