package com.cydeo.service.impl;

import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.UserDto;
import com.cydeo.entity.Invoice;
import com.cydeo.entity.InvoiceProduct;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.DashboardService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
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
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;

    public DashboardServiceImpl(InvoiceProductServiceImpl invoiceProductService, InvoiceProductRepository invoiceProductRepository, InvoiceRepository invoiceRepository, SecurityService securityService, InvoiceService invoiceService, MapperUtil mapperUtil) {
        this.invoiceProductRepository = invoiceProductRepository;
        this.securityService = securityService;
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
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

    @Override
    public List<InvoiceDto> getLast3ApprovedInvoices() {
        Long companyId = securityService.getLoggedInUser().getCompany().getId();


        List<Invoice>  invoices = invoiceRepository.findTop3ByCompanyIdAndInvoiceStatusOrderByDateDesc(companyId,InvoiceStatus.APPROVED);
        List<InvoiceProduct> invoiceProducts = invoiceProductRepository.find3LastApprovedTransactionDesc(companyId);


        List<InvoiceDto> invoiceDtos= invoices.stream()
                .map(invoice-> mapperUtil.convert(invoice,new InvoiceDto()))
                .collect(Collectors.toList());



        for (int i = 0; i < 3; i++) {
            //Quantity of products
            BigDecimal quantity= BigDecimal.valueOf(invoiceProducts.get(i).getQuantity());
            //taxRate of products
            BigDecimal taxRate=BigDecimal.valueOf(invoiceProducts.get(i).getTax());
            //unit price of products
            BigDecimal unitPrice=invoiceProducts.get(i).getPrice();

            invoiceDtos.get(i).setPrice(quantity.multiply(unitPrice));
            invoiceDtos.get(i).setTax(quantity.multiply(unitPrice).multiply(taxRate.divide(BigDecimal.valueOf(100))));
            invoiceDtos.get(i).setTotal(quantity.multiply(unitPrice).add(quantity.multiply(unitPrice).divide(taxRate)));

        }


        return invoiceDtos;
    }



}
