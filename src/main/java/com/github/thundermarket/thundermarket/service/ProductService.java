package com.github.thundermarket.thundermarket.service;

import com.github.thundermarket.thundermarket.domain.Product;
import com.github.thundermarket.thundermarket.domain.ProductDetail;
import com.github.thundermarket.thundermarket.domain.ProductResponse;
import com.github.thundermarket.thundermarket.domain.ProductsResponse;
import com.github.thundermarket.thundermarket.repository.ProductDetailRepository;
import com.github.thundermarket.thundermarket.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    public static final int minPaginationLimit = 1;
    public static final int maxPaginationLimit = 100;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    public ProductService(ProductRepository userRepository, ProductDetailRepository productDetailRepository) {
        this.productRepository = userRepository;
        this.productDetailRepository = productDetailRepository;
    }

    public ProductsResponse products(Long cursorId, int limit) {
        long effectiveCursorId = (cursorId == null) ? 0 : cursorId;
        int effectiveLimit = Math.min(maxPaginationLimit, Math.max(minPaginationLimit, limit));

        List<Product> products = productRepository.findAll(effectiveCursorId, effectiveLimit);
        Long newCursorId = products.isEmpty() ? null : products.get(products.size() - 1).getId();
        long totalCount = productRepository.count();
        return ProductsResponse.of(products, newCursorId, limit, totalCount);
    }

    public ProductResponse add(Product product, ProductDetail productDetail) {
        return ProductResponse.of(productRepository.save(product), productDetailRepository.save(productDetail));
    }

    public Product update(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.delete(id);
    }
}
