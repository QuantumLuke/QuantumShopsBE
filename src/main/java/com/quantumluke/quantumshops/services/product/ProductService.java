package com.quantumluke.quantumshops.services.product;

import com.quantumluke.quantumshops.dto.ImageDto;
import com.quantumluke.quantumshops.dto.ProductDto;
import com.quantumluke.quantumshops.exceptions.AlreadyExistsException;
import com.quantumluke.quantumshops.exceptions.ResourceNotFoundException;
import com.quantumluke.quantumshops.models.Category;
import com.quantumluke.quantumshops.models.Image;
import com.quantumluke.quantumshops.models.Product;
import com.quantumluke.quantumshops.repository.CategoryRepository;
import com.quantumluke.quantumshops.repository.ImageRepository;
import com.quantumluke.quantumshops.repository.ProductRepository;
import com.quantumluke.quantumshops.request.AddProductRequest;
import com.quantumluke.quantumshops.request.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Override
    public Product addProduct(AddProductRequest request) {

        if (productExists(request.getName(), request.getBrand())) {
            throw new AlreadyExistsException("Product " + request.getBrand() + " " + request.getName() + " already exists");
        }
        Category category = Optional.ofNullable(
                categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                   Category newCategory = new Category(request.getCategory().getName());
                   return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProductFromRequest(request, category));
    }

    private boolean productExists(String name, String brand){
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProductFromRequest(AddProductRequest productRequest, Category category) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setBrand(productRequest.getBrand());
        product.setPrice(productRequest.getPrice());
        product.setInventory(productRequest.getInventory());
        product.setDescription(productRequest.getDescription());
        product.setCategory(category);
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(
                        productRepository::delete,
                        () -> { throw new ResourceNotFoundException("Product not found with id: " + id); }
                );
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long productId) {

        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProductFromRequest(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private Product updateExistingProductFromRequest(Product existingProduct, UpdateProductRequest request){
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;

    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImage(imageDtos);
        return productDto;
    }
}
