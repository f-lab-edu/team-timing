package com.github.thundermarket.thundermarket.TestDouble;

import com.github.thundermarket.thundermarket.domain.Product;
import com.github.thundermarket.thundermarket.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductFakeRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();

    @Override
    public List<Product> findAll(Long cursorId, int limit) {
        return products;
    }

    @Override
    public Product save(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(product.getId())) {
                products.set(i, product);
                return product;
            }
        }
        products.add(product);
        return product;
    }

    @Override
    public void delete(Long id) {
        products.remove(id.intValue());
    }

    @Override
    public long count() {
        return products.size();
    }
}
