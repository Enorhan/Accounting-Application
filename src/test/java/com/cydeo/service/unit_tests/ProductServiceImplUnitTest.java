package com.cydeo.service.unit_tests;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.enums.ProductUnit;
import com.cydeo.repository.InvoiceProductRepository;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.impl.ProductServiceImpl;
import com.cydeo.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplUnitTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InvoiceProductRepository invoiceProductRepository;
    @Mock
    private CompanyService companyService;
    @Mock
    private MapperUtil mapperUtil;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void should_find_all_in_stock() {

        Long mockCompanyId = 1L;
        when(companyService.getCompanyIdByLoggedInUser()).thenReturn(mockCompanyId);

        Product mockProduct1 = new Product();
        mockProduct1.setQuantityInStock(10);
        mockProduct1.setIsDeleted(false);
        mockProduct1.setId(1L);
        Product mockProduct2 = new Product();
        mockProduct2.setQuantityInStock(10);
        mockProduct2.setIsDeleted(false);
        mockProduct2.setId(2L);
        Product mockProduct3 = new Product();
        mockProduct3.setQuantityInStock(0);
        mockProduct3.setIsDeleted(false);
        mockProduct3.setId(3L);
        Product mockProduct4 = new Product();
        mockProduct4.setQuantityInStock(0);// This product is out of stock
        mockProduct4.setIsDeleted(false);
        mockProduct4.setId(4L);

          List<Product> mockProducts = Arrays.asList(mockProduct1, mockProduct2, mockProduct3, mockProduct4);
          when(productRepository.findAllByCompanyIdAndIsDeleted(1L, false)).thenReturn(mockProducts);


        List<ProductDto> result = productService.findAllInStock();


        assertEquals(2, result.size());
    }


    @Test
    public void should_throw_exception_when_not_find_By_Product_id() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(() ->
                productService.findById(1L));
        assertThat(throwable).hasMessage("Product not found with id: " + 1L);
        assertThat(throwable).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testIsInStock_ProductInStock() {

        Long productId = 1L;
        Product product = new Product();
        product.setQuantityInStock(10); // Assume the product is in stock
        when(productRepository.findProductById(productId)).thenReturn(product);


        Boolean result = productService.isInStock(productId);


        assertTrue(result);
        verify(productRepository).findProductById(productId);
    }

    @Test
    void testIsInStock_ProductOutOfStock() {

        Long productId = 1L;
        Product product = new Product();
        product.setQuantityInStock(0); // Assume the product is out of stock
        when(productRepository.findProductById(productId)).thenReturn(product);


        Boolean result = productService.isInStock(productId);


        assertFalse(result);
        verify(productRepository).findProductById(productId);
    }

    @Test
    void testHasInvoice_InvoiceExists() {

        Long productId = 1L;
        when(invoiceProductRepository.existsByProductId(productId)).thenReturn(true);


        Boolean result = productService.hasInvoice(productId);


        assertTrue(result);
        verify(invoiceProductRepository).existsByProductId(productId);
    }

    @Test
    void testHasInvoice_InvoiceDoesNotExist() {

        Long productId = 1L;
        when(invoiceProductRepository.existsByProductId(productId)).thenReturn(false);


        Boolean result = productService.hasInvoice(productId);


        assertFalse(result);
        verify(invoiceProductRepository).existsByProductId(productId);
    }

    @Test
    void testDelete_ProductExists() {

        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setIsDeleted(false); // Initially, the product is not deleted
        when(productRepository.findProductById(productId)).thenReturn(product);


        productService.delete(productId);


        assertTrue(product.getIsDeleted()); // Check that the product is marked as deleted
        verify(productRepository).findProductById(productId);
        verify(productRepository).save(product);
    }

    @Test
    void testDelete_ProductNotFound() {

        Long productId = 1L;
        when(productRepository.findProductById(productId)).thenReturn(null);


        assertThrows(NullPointerException.class, () -> {
            productService.delete(productId);
        });

        verify(productRepository).findProductById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }



    @Test
    void testUpdate() {

        Long productId = 1L;
        ProductDto productDto = new ProductDto();
        productDto.setLowLimitAlert(5);
        productDto.setCategory(new CategoryDto());
        productDto.setName("Updated Product");
        productDto.setProductUnit(ProductUnit.KG);

        Product product = new Product();
        when(productRepository.findProductById(productId)).thenReturn(product);


        productService.update(productDto, productId);


        assertEquals(productDto.getLowLimitAlert(), product.getLowLimitAlert());
        assertEquals(productDto.getName(), product.getName());
        assertEquals(productDto.getProductUnit(), product.getProductUnit());


        verify(productRepository).findProductById(productId);
        verify(productRepository).save(product);
    }

    @Test
    void testUpdate_ProductNotFound() {

        Long productId = 1L;
        ProductDto productDto = new ProductDto();
        when(productRepository.findProductById(productId)).thenReturn(null);


        assertThrows(NullPointerException.class, () -> {
            productService.update(productDto, productId);
        });

        verify(productRepository).findProductById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }


}




