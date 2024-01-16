package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.ProductSize;
import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }



    ////////// 상품 추가 ////////////
    // 클라이언트에서 보낸 addProductDto 로 Product 를 만듦
    public Product createProductFromDto(AddProductDto addProductDto) {
        Product product = new Product(addProductDto);

        List<String> selectedSizes = addProductDto.getSizes();
        for (String selectedSize : selectedSizes) {
            product.setSizeAvailable(selectedSize);
        }
        return product;
    }


    ////////// 상품 수정 ////////////
    // 상품수정으로 보내기 위한 product 로 addProductDto 만듦
    public AddProductDto createAddProductDto(Long productId) {
        Product product = findById(productId).orElse(null);
        if(product == null) {
            return null;
        }
        AddProductDto addProductDto = new AddProductDto();
        addProductDto.setName(product.getName());
        addProductDto.setPrice(product.getPrice());
        addProductDto.setDescription(product.getDescription());
        updateSize(product.getSizes(), addProductDto);
        return addProductDto;
    }

    private void updateSize(List<ProductSize> sizes, AddProductDto addProductDto) {
        for (ProductSize size : sizes) {
            if (size.isAvailable()) {
                addProductDto.getSizes().add(size.getSize());
            }
        }
    }


    // 상품수정폼에서 받은 addProductDto 로 상품 수정
    public boolean updateWithAddProductDto(Long id, AddProductDto addProductDto) {
        Product product = findById(id).orElse(null);
        if(product == null) return false;

        product.setName(addProductDto.getName());
        product.setPrice(addProductDto.getPrice());
        product.setDescription(product.getDescription());
        product.updateAvailableSizes(addProductDto.getSizes());

        return true;
    }


}
