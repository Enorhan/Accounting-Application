package com.cydeo.service.integration_tests;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.enums.ProductUnit;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
public class ProductServiceImplIntegrationTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private InvoiceProductRepository invoiceProductRepository;
    @Autowired
    private SecurityService securityService;


    @Test
    public void test_FindAllInStock() {

        Long companyId = securityService.getLoggedInUser().getId();


        List<ProductDto> productsInStock = productService.findAllInStock();


        assertNotNull(productsInStock);
        assertEquals(1, productsInStock.size());



    }
    @Test
    public void test_findAllByCurrentCompany(){
        Long companyId=companyService.getCompanyIdByLoggedInUser();
        List<Product> productList= productRepository.findAllByCompanyIdAndIsDeleted(companyId,false);
        assertThat(productList.size()).isEqualTo(4);


    }
    @Test
    public void test_findById(){

        List<Product> productList= productRepository.findAllByCompanyIdAndIsDeleted(2L,false);


        Throwable throwable = catchThrowable(() -> productService.findById(400L));
        assertThat(throwable).isInstanceOf(NoSuchElementException.class);
        assertThat(throwable).hasMessage("Product not found with id: " + 400L);

        ProductDto productDto=productService.findById(7L);

        assertThat(productDto.getName()).isEqualTo("Moto G Power");
        assertNotNull(productDto);
    }
    @Test
    public void test_save(){
        Product product=new Product();
        productRepository.save(product);

        ProductDto productDto =new ProductDto(100L,"Apple",10,5, ProductUnit.PCS, new CategoryDto(),false);


        productService.save(productDto);

        Product savedProduct = productRepository.findProductById(100L);
        assertNotNull(savedProduct);
    }








    }



