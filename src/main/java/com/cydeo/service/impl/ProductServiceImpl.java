package com.cydeo.service.impl;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Product;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;
    private final InvoiceProductRepository invoiceProductRepository;

    public ProductServiceImpl(ProductRepository productRepository, MapperUtil mapperUtil, CompanyService companyService, InvoiceProductRepository invoiceProductRepository) {
        this.productRepository = productRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
        this.invoiceProductRepository = invoiceProductRepository;
    }

    @Override
    public List<ProductDto> findAllInStock() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        return productRepository.findAllByCompanyIdAndIsDeleted(companyId,false).stream()
                .filter(product -> product.getQuantityInStock() > 0)
                .map(product -> mapperUtil.convert(product, new ProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> findAllByCurrentCompany() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        return productRepository.findAllByCompanyIdAndIsDeleted(companyId, false).stream()
                .map(product -> {
                    ProductDto productDto = mapperUtil.convert(product, new ProductDto());
                    productDto.setHasInvoiceProduct(hasInvoice(product.getId()));
                    return productDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto findById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));

        return mapperUtil.convert(product, new ProductDto());
    }

    @Override
    public void save(ProductDto productDto) {
        Product product=mapperUtil.convert(productDto,new Product());
        productRepository.save(product);
    }

    @Override
    public void update(ProductDto productDto,Long productId) {

        Product product=productRepository.findProductById(productId);
        product.setLowLimitAlert(productDto.getLowLimitAlert());
        product.setCategory(mapperUtil.convert(productDto.getCategory(),new Category()));
        product.setName(productDto.getName());
        product.setProductUnit(productDto.getProductUnit());

        productRepository.save(product);
    }

    @Override
    public void delete(Long productId) {
        Product product=productRepository.findProductById(productId);
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Override
    public Boolean hasInvoice(Long productId) {
        return invoiceProductRepository.existsByProductId(productId);
    }

    @Override
    public Boolean isInStock(Long productId) {
        return productRepository.findProductById(productId).getQuantityInStock()>0;
    }
}