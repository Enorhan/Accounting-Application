package com.cydeo.service.impl;

import com.cydeo.dto.ProductDto;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;

    public ProductServiceImpl(ProductRepository productRepository, MapperUtil mapperUtil, CompanyService companyService) {
        this.productRepository = productRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
    }

    @Override
    public List<ProductDto> findAllInStock() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();

        List<ProductDto> collect = productRepository.findAllByCompanyId(companyId).stream()
                .filter(product -> product.getQuantityInStock() > 0)
                .map(product -> mapperUtil.convert(product, new ProductDto()))
                .collect(Collectors.toList());

        return collect;
    }
}