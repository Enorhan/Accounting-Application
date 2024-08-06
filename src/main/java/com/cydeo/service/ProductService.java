package com.cydeo.service;

import com.cydeo.dto.ProductDto;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface ProductService {
    List<ProductDto> findAllInStock();
    List<ProductDto> findAllByCurrentCompany();
    ProductDto findById(Long id);
    void save(ProductDto productDto);
    void update(ProductDto productDto,Long productId);
    void delete(Long productId);
    Boolean hasInvoice(Long productId);
    Boolean isInStock(Long productId);


    void checkProductStock(Long productId, int requiredQuantity);
    Boolean checkIfProductNameAlreadyExists(String productName, BindingResult bindingResult);
}
