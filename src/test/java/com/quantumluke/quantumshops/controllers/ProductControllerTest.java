package com.quantumluke.quantumshops.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.quantumluke.quantumshops.dto.ProductDto;
import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.Product;
import com.quantumluke.quantumshops.request.AddProductRequest;
import com.quantumluke.quantumshops.request.UpdateProductRequest;
import com.quantumluke.quantumshops.response.ApiResponse;
import com.quantumluke.quantumshops.services.product.IProductService;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ProductControllerTests {
    private IProductService productService;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        productService = mock(IProductService.class);
        productController = new ProductController(productService);
    }

    @DisplayName("Should return all products successfully")
    @Test
    void getAllProducts_ReturnsAllProducts() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        List<ProductDto> productDtos = Arrays.asList(new ProductDto(), new ProductDto());
        when(productService.getAllProducts()).thenReturn(products);
        when(productService.getConvertedProducts(products)).thenReturn(productDtos);

        ResponseEntity<ApiResponse> response = productController.getAllProducts();

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products fetched successfully", response.getBody().getMessage());
        assertEquals(productDtos, response.getBody().getData());
    }

    @DisplayName("Should handle exception on getAllProducts")
    @Test
    void getAllProducts_ThrowsException_ReturnsServerError() {
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<ApiResponse> response = productController.getAllProducts();

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().startsWith("Error fetching products: "));
    }

    @DisplayName("Should return product by ID when found")
    @Test
    void getProductById_ExistingProduct_ReturnsProduct() {
        Product product = new Product();
        ProductDto dto = new ProductDto();
        when(productService.getProductById(1L)).thenReturn(product);
        when(productService.convertToDto(product)).thenReturn(dto);

        ResponseEntity<ApiResponse> response = productController.getProductById(1L);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product fetched successfully", response.getBody().getMessage());
        assertEquals(dto, response.getBody().getData());
    }

    @DisplayName("Should return not found for non-existing product ID")
    @Test
    void getProductById_NonExistingProduct_ReturnsNotFound() {
        when(productService.getProductById(2L)).thenThrow(new ResourceNotFoundException("Product not found"));

        ResponseEntity<ApiResponse> response = productController.getProductById(2L);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @DisplayName("Should return server error for exception in getProductById")
    @Test
    void getProductById_Exception_ReturnsServerError() {
        when(productService.getProductById(3L)).thenThrow(new RuntimeException("failure!"));

        ResponseEntity<ApiResponse> response = productController.getProductById(3L);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().startsWith("Error fetching product: "));
    }

    @DisplayName("Should return products by category")
    @Test
    void getProductsByCategory_ReturnsProducts() {
        String category = "Electronics";
        List<Product> products = Collections.singletonList(new Product());
        List<ProductDto> productDtos = Collections.singletonList(new ProductDto());
        when(productService.getProductsByCategory(category)).thenReturn(products);
        when(productService.getConvertedProducts(products)).thenReturn(productDtos);

        ResponseEntity<ApiResponse> response = productController.getProductsByCategory(category);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products fetched successfully", response.getBody().getMessage());
        assertEquals(productDtos, response.getBody().getData());
    }

    @DisplayName("Should return products by brand")
    @Test
    void getProductsByBrand_ReturnsProducts() {
        String brand = "Apple";
        List<Product> products = Collections.singletonList(new Product());
        List<ProductDto> productDtos = Collections.singletonList(new ProductDto());
        when(productService.getProductsByBrand(brand)).thenReturn(products);
        when(productService.getConvertedProducts(products)).thenReturn(productDtos);

        ResponseEntity<ApiResponse> response = productController.getProductsByBrand(brand);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products fetched successfully", response.getBody().getMessage());
        assertEquals(productDtos, response.getBody().getData());
    }

    @DisplayName("Should return products by category and brand")
    @Test
    void getProductsByCategoryAndBrand_ReturnsProducts() {
        String category = "Electronics";
        String brand = "Apple";
        List<Product> products = Collections.singletonList(new Product());
        List<ProductDto> productDtos = Collections.singletonList(new ProductDto());
        when(productService.getProductsByCategoryAndBrand(category, brand)).thenReturn(products);
        when(productService.getConvertedProducts(products)).thenReturn(productDtos);

        ResponseEntity<ApiResponse> response = productController.getProductsByCategoryAndBrand(category, brand);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products fetched successfully", response.getBody().getMessage());
        assertEquals(productDtos, response.getBody().getData());
    }
    
    @DisplayName("Should return products by name")
    @Test
    void getProductsByName_ReturnsProducts() {
        String name = "iPhone";
        List<Product> products = Collections.singletonList(new Product());
        List<ProductDto> productDtos = Collections.singletonList(new ProductDto());
        when(productService.getProductsByName(name)).thenReturn(products);
        when(productService.getConvertedProducts(products)).thenReturn(productDtos);
        
        ResponseEntity<ApiResponse> response = productController.getProductsByName(name);
        
        assertNotNull(response.getBody());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products fetched successfully", response.getBody().getMessage());
        assertEquals(productDtos, response.getBody().getData());
    }
    
    @DisplayName("Should return success")
    @Test
    void addProduct_ReturnsSuccess() {
        Product newProduct = new Product();
        AddProductRequest request = new AddProductRequest();
        when(productService.addProduct(request)).thenReturn(newProduct);
        ResponseEntity<ApiResponse> response = productController.addProduct(request);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product added successfully", response.getBody().getMessage());
        assertEquals(newProduct, response.getBody().getData());
    }

    @DisplayName("Should return success")
    @Test
    void updateProduct_ReturnsSuccess() {
        Long productId = 1L;
        UpdateProductRequest request = new UpdateProductRequest();
        when(productService.updateProduct(productId, request));
        ResponseEntity<ApiResponse> response = productController.updateProduct(productId, request);

        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully", response.getBody().getMessage());
    }


    @DisplayName("Should return success")
    @Test
    void deleteProduct_ReturnSuccess(){
        Long productId = 1L;
        productService.deleteProductById(productId);
        ResponseEntity<ApiResponse> response = productController.deleteProductById(productId);
        assertNotNull(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}