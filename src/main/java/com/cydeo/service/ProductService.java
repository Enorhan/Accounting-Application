package com.cydeo.service;

import com.cydeo.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> findAllInStock();
    List<ProductDto> findAllByCurrentCompany();
    ProductDto findById(Long id);
    void save(ProductDto productDto);
}
