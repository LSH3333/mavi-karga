package com.lsh.mavikarga.domain;

import com.lsh.mavikarga.enums.ProductColor;
import com.lsh.mavikarga.enums.Sizes;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// 실제 상품 entity

@Entity
@Data
public class ProductSize implements Comparable<ProductSize> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_size_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productSize", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @OneToMany(mappedBy = "productSize")
    private List<Cart> carts = new ArrayList<>();

    // 제품 사이즈
//    private String size;
    @Enumerated(EnumType.STRING)
    private Sizes size;

    // 재고 여부
    private boolean available = false;
    // 색상
    @Enumerated(EnumType.STRING)
    private ProductColor productColor;




    public ProductSize() {}

    public ProductSize(Sizes size, ProductColor productColor, Product product) {
        this.size = size;
        this.productColor = productColor;
        this.product = product;
        this.available = false; // 최초에는 재고 없음 처리
    }

    // size 에 따라 S,M,L ... 순으로 정렬
    @Override
    public int compareTo(@NotNull ProductSize productSize) {
        List<String> customOrder = List.of("XS", "S", "M", "L", "XL", "XXL");

        int index1 = customOrder.indexOf(this.getSize());
        int index2 = customOrder.indexOf(productSize.getSize());

        return Integer.compare(index1, index2);
    }
}
