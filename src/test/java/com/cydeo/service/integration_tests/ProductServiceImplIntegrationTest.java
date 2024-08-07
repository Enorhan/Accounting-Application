package com.cydeo.service.integration_tests;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.ProductService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.SecuritySetUpUtil;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

    @BeforeEach
    void setUp() {
        SecuritySetUpUtil.setUpSecurityContext();
    }


    @Test
    public void test_FindAllInStock() {

        Long companyId = securityService.getLoggedInUser().getId();


        List<ProductDto> productsInStock = productService.findAllInStock();


        assertNotNull(productsInStock);
        assertEquals(1, productsInStock.size());

    }

    @Test
    public void test_findAllByCurrentCompany() {
        Long companyId = companyService.getCompanyIdByLoggedInUser();
        List<Product> productList = productRepository.findAllByCompanyIdAndIsDeleted(companyId, false);
        assertThat(productList.size()).isEqualTo(4);


    }

    @Test
    public void test_findById() {

        List<Product> productList = productRepository.findAllByCompanyIdAndIsDeleted(2L, false);


        Throwable throwable = catchThrowable(() -> productService.findById(400L));
        assertThat(throwable).isInstanceOf(NoSuchElementException.class);
        assertThat(throwable).hasMessage("Product not found with id: " + 400L);

        ProductDto productDto = productService.findById(7L);

        assertThat(productDto.getName()).isEqualTo("Moto G Power");
        assertNotNull(productDto);
    }

    @Test
    public void test_save() {
        ProductDto productDto = new ProductDto();
        productDto.setName("Desktop");

        ProductDto savedProductDto = productService.save(productDto);

        Product savedProduct = productRepository.findById(savedProductDto.getId()).orElse(null);

        assertNotNull(savedProduct);
        assertEquals("Desktop", savedProduct.getName());
        assertEquals(8L, savedProduct.getId());
    }

    @Test
    public void test_update() {
        ProductDto productDto = productService.findById(1L);
        productDto.setName("test");

        productService.update(productDto, productDto.getId());

        Product updatedProduct = productRepository.findProductById(productDto.getId());

        assertNotNull(updatedProduct);
        assertThat(updatedProduct.getName()).isEqualTo("test");
        assertEquals(8, updatedProduct.getQuantityInStock());
        assertEquals(1, updatedProduct.getCategory().getId());

    }

    @Test
    public void test_delete() {

        Product product = productRepository.findProductById(7L);

        productService.delete(product.getId());

        assertEquals(true, product.getIsDeleted());

    }
    @Test
    public void test_has_invoice_not_exist(){


        Boolean result = productService.hasInvoice(3L);

        assertFalse(result);
    }
    @Test
    public void test_invoice_exist(){
        Boolean result = productService.hasInvoice(1L);
        assertTrue(result);

    }
    @Test
    void testIsInStock_ProductInStock() {

        Product product=productRepository.findProductById(5L);


        Boolean result = productService.isInStock(product.getId());


        assertTrue(result);
        assertEquals(10,product.getQuantityInStock());
    }
    @Test
    void testIsInStock_ProductOutOfStock() {

        Product product=productRepository.findProductById(2L);


        Boolean result = productService.isInStock(product.getId());


        assertFalse(result);
        assertEquals(0,product.getQuantityInStock());
    }
    @Test
    public void test_checkIfProductNameAlreadyExists_whenExists() {
        ProductDto productDto = new ProductDto();
        productDto.setName("HP Elite 800G1 Desktop Computer Package");

        BindingResult bindingResult = new BeanPropertyBindingResult(productDto, "productDto");


        boolean exists = productService.checkIfProductNameAlreadyExists("HP Elite 800G1 Desktop Computer Package", bindingResult);

        assertTrue(exists, "Product name should exist");
        assertTrue(bindingResult.hasErrors(), "BindingResult should have errors");
        assertEquals("This product name: HP Elite 800G1 Desktop Computer Package already exists. Please try another name.",
                bindingResult.getFieldError("name").getDefaultMessage());
    }

    @Test
    public void test_checkIfProductNameAlreadyExists_whenNotExists() {

        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "productDto");


        boolean exists = productService.checkIfProductNameAlreadyExists("Non-Existing Product", bindingResult);


        assertFalse(exists, "Product name should not exist");
        assertFalse(bindingResult.hasErrors(), "BindingResult should not have errors");
    }



}



