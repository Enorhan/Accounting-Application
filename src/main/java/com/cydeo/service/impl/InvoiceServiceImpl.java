package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.InvoiceDto;
import com.cydeo.dto.InvoiceProductDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Company;
import com.cydeo.entity.Invoice;
import com.cydeo.enums.InvoiceStatus;
import com.cydeo.enums.InvoiceType;
import com.cydeo.exceptions.InvoiceNotFoundException;
import com.cydeo.exceptions.ProductLowLimitAlertException;
import com.cydeo.exceptions.ProductNotFoundException;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.InvoiceRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.InvoiceProductService;
import com.cydeo.service.InvoiceService;
import com.cydeo.service.ProductService;
import com.cydeo.util.InvoiceUtils;
import com.cydeo.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final ProductService productService;
    private final InvoiceProductService invoiceProductService;
    private final InvoiceProductRepository invoiceProductRepository;
    private final InvoiceUtils invoiceUtils;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, MapperUtil mapperUtil, CompanyService companyService, @Lazy InvoiceProductService invoiceProductService, ProductService productService, InvoiceProductRepository invoiceProductRepository, InvoiceUtils invoiceUtils) {
        this.invoiceRepository = invoiceRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.productService = productService;
        this.invoiceProductService = invoiceProductService;
        this.invoiceProductRepository = invoiceProductRepository;
        this.invoiceUtils = invoiceUtils;
    }

    public List<InvoiceDto> listAllInvoicesByType(InvoiceType invoiceType) {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        List<InvoiceDto> invoiceDtos = invoiceRepository.
                findAllByInvoiceTypeAndCompanyIdAndIsDeletedOrderByInvoiceNoDesc(invoiceType, companyId, false).stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .collect(Collectors.toList());

        for (InvoiceDto eachInvoiceDto : invoiceDtos) {
            invoiceUtils.calculateTotalsForInvoice(eachInvoiceDto);
        }

        return invoiceDtos;
    }

    @Override
    public List<InvoiceDto> findAllByInvoiceTypeAndInvoiceStatusAndCompanyIdAndIsDeleted(InvoiceType invoiceType, InvoiceStatus invoiceStatus, Long companyId, boolean isDeleted) {
        return invoiceRepository
                .findAllByInvoiceTypeAndInvoiceStatusAndCompanyIdAndIsDeleted(invoiceType, InvoiceStatus.APPROVED, companyId, false)
                .stream()
                .map(invoice -> mapperUtil.convert(invoice, new InvoiceDto()))
                .toList();
    }

    @Override
    public InvoiceDto findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));

        InvoiceDto invoiceDto = mapperUtil.convert(invoice, new InvoiceDto());

        invoiceUtils.calculateTotalsForInvoice(invoiceDto);

        return invoiceDto;
    }

    @Override
    public String getNewPurchaseInvoiceNumberId() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        String lastPurchaseInvoiceNumberId = invoiceRepository
                .findAllByInvoiceTypeAndCompanyIdOrderByInvoiceNoDesc(InvoiceType.PURCHASE, companyId)
                .get(0)
                .getInvoiceNo();

        if (lastPurchaseInvoiceNumberId == null) {
            return "P-001";
        }

        String nextPurchaseInvoiceNumberId;
        long nextPurchaseInvoiceId;

        String[] dividedLastInvoiceNumber = lastPurchaseInvoiceNumberId.split("-");

        nextPurchaseInvoiceId = Long.parseLong(dividedLastInvoiceNumber[1]) + 1;

        nextPurchaseInvoiceNumberId = String.format("P-%03d", nextPurchaseInvoiceId);

        return nextPurchaseInvoiceNumberId;
    }

    @Override
    public void save(InvoiceDto invoiceDto, InvoiceType invoiceType) {
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        CompanyDto companyDto = companyService.getCompanyDtoByLoggedInUser();
        Company company = mapperUtil.convert(companyDto, new Company());

        invoice.setInvoiceType(invoiceType);
        invoice.setCompany(company);
        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);

        invoiceRepository.save(invoice);
    }

    @Override
    public InvoiceDto update(InvoiceDto invoiceDto, Long id) {
        Invoice oldInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));

        invoiceDto.setId(id);
        Invoice invoice = mapperUtil.convert(invoiceDto, new Invoice());

        invoice.setInvoiceStatus(oldInvoice.getInvoiceStatus());
        invoice.setInvoiceType(oldInvoice.getInvoiceType());
        invoice.setCompany(oldInvoice.getCompany());

        invoiceRepository.save(invoice);

        return findById(invoiceDto.getId());
    }


    @Override
    @Transactional
    public String createNewSalesInvoiceNo() {

        String company = companyService.getCurrentCompanyTitle();
        Invoice latestInvoice = invoiceRepository.findTopSalesInvoice(company);

        if (latestInvoice == null) {
            return "S-001";
        } else {

            String latestInvoiceNo = latestInvoice.getInvoiceNo();
            int latestNumber = Integer.parseInt(latestInvoiceNo.substring(2));

            return "S-" + String.format("%03d", latestNumber + 1);
        }
    }

    @Override
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));

        invoice.setIsDeleted(true);

        invoiceProductService.deleteByInvoiceId(id);
        invoiceRepository.save(invoice);
    }

    @Override
    public void approvePurchaseInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + invoiceId));

        List<InvoiceProductDto> invoiceProductsDto = invoiceProductService.findAllByInvoiceIdAndIsDeleted(invoiceId, false);

        for (InvoiceProductDto invoiceProduct : invoiceProductsDto) {
            ProductDto productDto = productService.findById(invoiceProduct.getProduct().getId());
            productDto.setQuantityInStock(productDto.getQuantityInStock() + invoiceProduct.getQuantity());

            invoiceProduct.setRemainingQuantity(invoiceProduct.getQuantity());
            invoiceProduct.setProfitLoss(BigDecimal.ZERO);

            invoiceProductService.save(invoiceProduct, invoiceId);


            productService.save(productDto);
        }

        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);

        invoiceRepository.save(invoice);
    }

    public List<Invoice> findTop3InvoicesByInvoiceStatus(InvoiceStatus invoiceStatus) {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        return invoiceRepository.findTop3ByCompanyIdAndInvoiceStatusOrderByInsertDateTimeDesc(companyId, invoiceStatus);
    }

    @Override
    public void approveSalesInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));

        List<InvoiceProductDto> invoiceProducts = invoiceProductService.findAllByInvoiceIdAndIsDeleted(id, false);
        boolean lowLimitAlert = false;
        String lowLimitAlertMessage = "";

        Map<Long, Integer> productQuantities = new HashMap<>();
        for (InvoiceProductDto invoiceProductDto : invoiceProducts) {
            Long productId = invoiceProductDto.getProduct().getId();
            int quantityToSell = invoiceProductDto.getQuantity();
            productQuantities.put(productId, productQuantities.getOrDefault(productId, 0) + quantityToSell);
        }

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            int totalQuantityToSell = entry.getValue();
            ProductDto productDto = productService.findById(productId);
            int totalAvailableStock = productDto.getQuantityInStock();

            if (totalQuantityToSell > totalAvailableStock) {
                throw new ProductNotFoundException("Stock of " + productDto.getName() + " is insufficient. Available: " + totalAvailableStock + ", Required: " + totalQuantityToSell + ". Please update the invoice.");
            }

            if (totalAvailableStock - totalQuantityToSell < productDto.getLowLimitAlert()) {
                lowLimitAlert = true;
                lowLimitAlertMessage = "Stock of " + productDto.getName() + " will go below the low limit if this invoice is approved. Available: " + totalAvailableStock + ", After sale: " + (totalAvailableStock - totalQuantityToSell);
            }
        }

        for (InvoiceProductDto invoiceProductDto : invoiceProducts) {
            int quantityToSell = invoiceProductDto.getQuantity();
            BigDecimal salePrice = invoiceProductDto.getPrice();

            List<InvoiceProductDto> purchaseProducts = invoiceProductRepository.findPurchaseProducts(
                            companyService.getCompanyIdByLoggedInUser(),
                            InvoiceType.PURCHASE,
                            InvoiceStatus.APPROVED,
                            invoiceProductDto.getProduct().getId()).stream()
                    .map(purchaseProduct -> mapperUtil.convert(purchaseProduct, new InvoiceProductDto()))
                    .toList();

            calculateTotalCostAndSetProfitLoss(purchaseProducts, invoiceProductDto, quantityToSell, salePrice);

            invoiceProductService.saveSalesInvoice(invoiceProductDto);

            ProductDto productDto = productService.findById(invoiceProductDto.getProduct().getId());
            int newQuantityInStock = productDto.getQuantityInStock() - invoiceProductDto.getQuantity();
            productDto.setQuantityInStock(newQuantityInStock);
            productService.save(productDto);
        }

        invoice.setInvoiceStatus(InvoiceStatus.APPROVED);
        invoice.setDate(LocalDate.now());
        invoiceRepository.save(invoice);

        if (lowLimitAlert) {
            throw new ProductLowLimitAlertException(lowLimitAlertMessage);
        }
    }

    @Override
    public InvoiceDto findByInvoiceNo(String invoiceNo) {
        Long companyId = companyService.getCompanyIdByLoggedInUser();
        return mapperUtil.convert(invoiceRepository.findByInvoiceNoAndCompanyId(invoiceNo, companyId), new InvoiceDto());
    }

    @Override
    public Boolean isQuantityAvailable(InvoiceProductDto invoiceProductDto) {
        ProductDto productDto = invoiceProductDto.getProduct();

        return productDto != null && invoiceProductDto.getQuantity() != null
                && invoiceProductDto.getQuantity() > productDto.getQuantityInStock();
    }

    private void calculateTotalCostAndSetProfitLoss(List<InvoiceProductDto> purchaseProducts, InvoiceProductDto invoiceProductDto, int quantityToSell, BigDecimal salePrice) {
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalCostForEachItem = BigDecimal.ZERO;

        for (InvoiceProductDto purchaseProduct : purchaseProducts) {
            int availableQuantity = purchaseProduct.getRemainingQuantity();
            if (availableQuantity > 0) {
                int quantityToUse = Math.min(quantityToSell, availableQuantity);
                BigDecimal costPrice = purchaseProduct.getPrice();

                BigDecimal taxToBeAdded = costPrice.multiply(BigDecimal.valueOf(0.01).multiply(BigDecimal.valueOf(purchaseProduct.getTax()))).multiply(BigDecimal.valueOf(purchaseProduct.getQuantity()));
                BigDecimal cost = (costPrice.multiply(BigDecimal.valueOf(purchaseProduct.getQuantity())).add(taxToBeAdded));
                totalCost = totalCost.add(cost);
                totalCostForEachItem = totalCost.divide(BigDecimal.valueOf(purchaseProduct.getQuantity()), 2, RoundingMode.HALF_UP);

                purchaseProduct.setRemainingQuantity(availableQuantity - quantityToUse);
                invoiceProductService.saveSalesInvoice(purchaseProduct);

                quantityToSell -= quantityToUse;
                if (quantityToSell == 0) {
                    break;
                }
            }
        }

        BigDecimal taxToBeAdded = salePrice.multiply(BigDecimal.valueOf(0.01).multiply(BigDecimal.valueOf(invoiceProductDto.getTax()).multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity()))));
        BigDecimal totalSale = (salePrice.multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity()))).add(taxToBeAdded);
        BigDecimal profitLoss = totalSale.subtract(totalCostForEachItem.multiply(BigDecimal.valueOf(invoiceProductDto.getQuantity())));
        invoiceProductDto.setProfitLoss(profitLoss);
    }
}
