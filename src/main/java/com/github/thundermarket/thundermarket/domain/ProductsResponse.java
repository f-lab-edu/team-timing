package com.github.thundermarket.thundermarket.domain;

import java.util.List;
import java.util.Objects;

public class ProductsResponse {

    private final List<Product> products;
    private final Long cursorId;
    private final int limit;
    private final long totalCount;


    public ProductsResponse(List<Product> products, Long cursorId, int limit, long totalCount) {
        this.products = products;
        this.cursorId = cursorId;
        this.limit = limit;
        this.totalCount = totalCount;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Long getCursorId() {
        return cursorId;
    }

    public int getLimit() {
        return limit;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public static ProductsResponse of(List<Product> products, Long newCursorId, int limit, Long totalCount) {
        return new ProductsResponse(products, newCursorId, limit, totalCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductsResponse that = (ProductsResponse) o;
        return Objects.equals(products, that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(products);
    }

    @Override
    public String toString() {
        return "ProductsResponse{" +
                "products=" + products +
                '}';
    }
}
