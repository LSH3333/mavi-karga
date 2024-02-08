package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.Product;
import com.lsh.mavikarga.domain.ProductSize;
import com.lsh.mavikarga.dto.AddProductDto;
import com.lsh.mavikarga.dto.ClothingDto;
import com.lsh.mavikarga.dto.ClothingDtoList;
import com.lsh.mavikarga.enums.ClothingCategory;
import com.lsh.mavikarga.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final LocaleResolver localeResolver;

    @Autowired
    public ProductService(ProductRepository productRepository, LocaleResolver localeResolver) {
        this.productRepository = productRepository;
        this.localeResolver = localeResolver;
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

    // 관리자가 제외하지 않은 제품들중 카테고리로 찾음
    public List<Product> findByClothingCategoryAndRemovedFalse(ClothingCategory clothingCategory) {
        return productRepository.findByClothingCategoryAndRemovedFalse(clothingCategory);
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
        addProductDto.setPrice_USD(product.getPrice_USD());
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
        product.setPrice_USD(addProductDto.getPrice_USD());
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


    ////////// 전체 상품 페이지 ////////////
    // 전체 상품 페이지에 보낼 DTO 생성
    public ClothingDtoList createClothingDto(List<Product> productList, HttpServletRequest request) {
        ClothingDtoList clothingDtoList = new ClothingDtoList();
        for (Product product : productList) {
            ClothingDto clothingDto = new ClothingDto(product.getId(), product.getName(), getPriceByLocale(request, product), product.getThumbnail_front().getUrl(),
                    product.getThumbnail_back().getUrl());
            clothingDtoList.getClothingDtoList().add(clothingDto);
        }
        return clothingDtoList;
    }

    // 현재 Locale 확인해서 그에 맞는 상품 가격 리턴
    private int getPriceByLocale(HttpServletRequest request, Product product) {
        Locale currentLocale = localeResolver.resolveLocale(request);
        if(currentLocale == Locale.US) {
            return product.getPrice_USD();
        } else {
            return product.getPrice();
        }
    }
}
