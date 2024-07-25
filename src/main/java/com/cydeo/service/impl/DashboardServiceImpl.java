package com.cydeo.service.impl;

import com.cydeo.dto.UserDto;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.entity.User;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.DashboardService;
import com.cydeo.service.SecurityService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceProductRepository invoiceProductRepository;
    private final SecurityService securityService;

    public DashboardServiceImpl(InvoiceProductServiceImpl invoiceProductService, InvoiceProductRepository invoiceProductRepository, InvoiceRepository invoiceRepository, SecurityService securityService) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.securityService = securityService;
    }

    @Override
    public Map<String, BigDecimal> getTotalCostAndSalesAndProfit_loss() {

        UserDto loggedInUser=securityService.getLoggedInUser();

        //to retrieve purchased invoices total cost
         List<InvoiceProduct> purchasedInvoices =invoiceProductRepository.findAll().stream()
                 .filter(p->p.getInvoice().getCompany().getTitle().equals(loggedInUser.getCompany().getTitle()))
                 .filter(invoice-> invoice.getInvoice().getInvoiceType()==InvoiceType.PURCHASE && invoice.getInvoice().getInvoiceStatus()== InvoiceStatus.APPROVED)
                 .collect(Collectors.toList());
        BigDecimal totalCost= BigDecimal.valueOf(0);
         for (InvoiceProduct invoice : purchasedInvoices) {
            int quantity= invoice.getQuantity();
            BigDecimal price = invoice.getPrice();
            int tax= invoice.getTax();

            totalCost=totalCost.add(price.multiply(BigDecimal.valueOf(quantity)).add(price.multiply(BigDecimal.valueOf(quantity)).divide(BigDecimal.valueOf(tax))));
         }

         //to retrieve sales invoices total
        List<InvoiceProduct> salesInvoices =invoiceProductRepository.findAll().stream()
                .filter(p->p.getInvoice().getCompany().getTitle().equals(loggedInUser.getCompany().getTitle()))
                .filter(invoice-> invoice.getInvoice().getInvoiceType()==InvoiceType.SALES && invoice.getInvoice().getInvoiceStatus()== InvoiceStatus.APPROVED)
                .collect(Collectors.toList());
        BigDecimal totalSalesCost= BigDecimal.valueOf(0);
        for (InvoiceProduct invoice : salesInvoices) {
            int quantity= invoice.getQuantity();
            BigDecimal price = invoice.getPrice();
            int tax= invoice.getTax();

            totalSalesCost=totalSalesCost.add(price.multiply(BigDecimal.valueOf(quantity)).add(price.multiply(BigDecimal.valueOf(quantity)).divide(BigDecimal.valueOf(tax))));
        }

        //to calculate total profit or loss
        List<BigDecimal> profitLoss =invoiceProductRepository.findAll().stream()
                .filter(p->p.getInvoice().getCompany().getTitle().equals(loggedInUser.getCompany().getTitle()))
                .map(invoice-> invoice.getProfitLoss()).collect(Collectors.toList());

        BigDecimal totalProfitLoss= BigDecimal.valueOf(0);
        for (BigDecimal profit_Loss : profitLoss) {

            totalProfitLoss=totalProfitLoss.add(profit_Loss);
        }


        Map<String,BigDecimal> map=new HashMap<>();
        map.put("totalCost",totalCost);
        map.put("totalSales",totalSalesCost);
        map.put("profitLoss",totalProfitLoss);

        return map;
    }



}
