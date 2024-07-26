package com.cydeo.service;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;

import java.util.List;

public interface ProductService {


    List<ProductDto>findAllInStockByCompanyId(Long companyId);
}
