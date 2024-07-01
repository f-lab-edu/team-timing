package com.github.thundermarket.thundermarket.TestDouble;

import com.github.thundermarket.thundermarket.domain.Product;

import java.util.ArrayList;
import java.util.List;

public class PaginatedProductFakeRepository extends ProductFakeRepository {

    private final List<Product> products = new ArrayList<>();

    public PaginatedProductFakeRepository() {
        initTestData();
    }

    private void initTestData() {
        // 테스트용 데이터 300개 추가
        for (int i = 0; i < 300; i++) {
            products.add(new Product.Builder()
                    .withId((long) i)
                    .withName("iPhone13")
                    .withPrice(300000)
                    .withStatus("For Sale")
                    .build());
        }
    }

    @Override
    public List<Product> findAll(Long cursorId, int limit) {
        List<Product> result = new ArrayList<>();
        long effectiveCursorId = (cursorId == null) ? 0 : cursorId;

        for (Product product : products) {
            if (product.getId() > effectiveCursorId) {
                result.add(product);
                if (result.size() == limit) {
                    break;  // 원하는 개수만큼 찾았으면 반복 중단
                }
            }
        }

        return result;
    }

    @Override
    public long count() {
        return products.size();
    }
}
