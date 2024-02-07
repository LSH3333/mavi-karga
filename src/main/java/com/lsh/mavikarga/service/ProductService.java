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
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    // product.removed=false 인 product 들 찾아 리턴
    public List<Product> findNotRemovedProducts() {
        return productRepository.findByRemovedFalse();
    }


    ////////// 상품 추가 ////////////
    // 클라이언트에서 보낸 addProductDto 로 Product 를 만듦
    public Product createProductFromDto(AddProductDto addProductDto) {
        return new Product(addProductDto);
    }


    ////////// 상품 수정 ////////////
    // 상품수정 폼 으로 보내기 위해 product 로 addProductDto 만듦
    public AddProductDto createAddProductDto(UUID productId) {
        Product product = findById(productId).orElse(null);
        if(product == null) {
            return null;
        }
        AddProductDto addProductDto = new AddProductDto();
        addProductDto.setName(product.getName());
        addProductDto.setPrice(product.getPrice());
        addProductDto.setDetailsAndCare(product.getDetailsAndCare());
        addProductDto.setDescription(product.getDescription());
        addProductDto.setClothingCategory(product.getClothingCategory());

        updateSize(product.getSizes(), addProductDto);
        updateColor(product.getSizes(), addProductDto);

        return addProductDto;
    }
    // addProductDto.sizes 리스트에 재고 있는 사이즈 추가
    private void updateSize(List<ProductSize> sizes, AddProductDto addProductDto) {
        for (ProductSize size : sizes) {
            if (size.isAvailable()) {
                addProductDto.getSizes().add(size.getSize());
            }
        }
    }
    private void updateColor(List<ProductSize> sizes, AddProductDto addProductDto) {
        for (ProductSize size : sizes) {
            if (size.isAvailable()) {
                addProductDto.getProductColor().add(size.getProductColor());
            }
        }
    }


    // 상품수정폼에서 받은 addProductDto 로 상품 수정
    public boolean updateWithAddProductDto(UUID id, AddProductDto addProductDto) {
        Product product = findById(id).orElse(null);
        if(product == null) return false;

        product.setName(addProductDto.getName());
        product.setPrice(addProductDto.getPrice());
        product.setDescription(addProductDto.getDescription());
        product.setDetailsAndCare(addProductDto.getDetailsAndCare());
        product.setClothingCategory(addProductDto.getClothingCategory());
        product.updateAvailableSizes(addProductDto.getSizes(), addProductDto.getProductColor());


        return true;
    }

    ////////// 상품 제거 ////////////
    // 객체를 제거하지는 않고, product.removed=true 로 만듦
    // todo: 연관된 ProductImage 들은 지울지 말지 고민 중
    public void makeProductRemovedTrue(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null) return;
        product.setRemoved(true);
    }




}
