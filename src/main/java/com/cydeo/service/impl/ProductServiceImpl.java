package com.cydeo.service.impl;

import com.cydeo.dto.ProductDto;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.ProductService;
import com.cydeo.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MapperUtil mapperUtil;

    public ProductServiceImpl(ProductRepository productRepository, MapperUtil mapperUtil) {
        this.productRepository = productRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<ProductDto> findAllInStockByCompanyId(Long companyId) {
        return productRepository.findAllByCompanyId(companyId).stream()
                .filter(product -> product.getQuantityInStock()>0)
                .map(product -> mapperUtil.convert(product,new ProductDto()))
                .collect(Collectors.toList());
    }
}
