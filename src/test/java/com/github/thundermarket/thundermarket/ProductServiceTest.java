package com.github.thundermarket.thundermarket;

import com.github.thundermarket.thundermarket.TestDouble.PaginatedProductFakeRepository;
import com.github.thundermarket.thundermarket.TestDouble.ProductDetailFakeRepository;
import com.github.thundermarket.thundermarket.TestDouble.ProductFakeRepository;
import com.github.thundermarket.thundermarket.domain.Product;
import com.github.thundermarket.thundermarket.domain.ProductDetail;
import com.github.thundermarket.thundermarket.domain.ProductsResponse;
import com.github.thundermarket.thundermarket.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class ProductServiceTest {

    private final ProductFakeRepository productRepository = new ProductFakeRepository();
    private final PaginatedProductFakeRepository paginatedProductFakeRepository = new PaginatedProductFakeRepository();
    private final ProductDetailFakeRepository productDetailFakeRepository = new ProductDetailFakeRepository();

    public Product createProduct() {
        return new Product.Builder()
                .withId(1L)
                .withName("iPhone12")
                .withPrice(200_000)
                .withStatus("판매중")
                .build();
    }

    public ProductDetail createProductDetail() {
        return new ProductDetail.Builder()
                .withId(1L)
                .withColor("white")
                .withBatteryCondition("80%")
                .withCameraCondition("good")
                .withDeliveryFee(3000)
                .build();
    }

    @Test
    public void 상품0개_상품목록_조회() {
        ProductService productService = new ProductService(productRepository, productDetailFakeRepository);
        ProductsResponse products = productService.products(0L, 10);

        Assertions.assertThat(products.getProducts().isEmpty()).isEqualTo(true);
    }

    @Test
    public void 상품1개_상품목록_조회() {
        ProductService productService = new ProductService(productRepository, productDetailFakeRepository);
        Product product = createProduct();
        productService.add(product, createProductDetail());

        ProductsResponse products = productService.products(0L, 10);

        Assertions.assertThat(products.getProducts().getFirst()).isEqualTo(product);
    }

    @Test
    public void 상품2개_상품목록_조회() {
        ProductService productService = new ProductService(productRepository, productDetailFakeRepository);
        Product product = createProduct();
        productService.add(product, createProductDetail());

        Long id2 = 2L;
        String name2 = "아이폰13";
        int price2 = 300000;
        String status2 = "판매중";
        Product product2 = new Product.Builder()
                .withId(id2)
                .withName(name2)
                .withPrice(price2)
                .withStatus(status2)
                .build();
        productService.add(product2, createProductDetail());

        ProductsResponse products = productService.products(0L, 10);
        System.out.println(products);

        Assertions.assertThat(products.getProducts().getFirst()).isEqualTo(product);
        Assertions.assertThat(products.getProducts().get(1)).isEqualTo(product2);
    }

    @Test
    public void 상품1개_상품목록_조회_후_상품1개추가_다시조회() {
        ProductService productService = new ProductService(productRepository, productDetailFakeRepository);
        Product product = createProduct();
        productService.add(product, createProductDetail());
        ProductsResponse products = productService.products(0L, 10);
        Assertions.assertThat(products.getProducts().getFirst()).isEqualTo(product);

        Long id2 = 2L;
        String name2 = "아이폰13";
        int price2 = 300000;
        String status2 = "판매중";
        Product product2 = new Product.Builder()
                .withId(id2)
                .withName(name2)
                .withPrice(price2)
                .withStatus(status2)
                .build();
        productService.add(product2, createProductDetail());
        ProductsResponse products2 = productService.products(0L, 10);
        Assertions.assertThat(products2.getProducts().get(1)).isEqualTo(product2);
    }

    @Test
    public void 상품1개_상품목록_조회_후_상품정보수정_다시조회() {
        ProductService productService = new ProductService(productRepository, productDetailFakeRepository);
        Product product = createProduct();
        productService.add(product, createProductDetail());
        ProductsResponse products = productService.products(0L, 10);
        Assertions.assertThat(products.getProducts().getFirst()).isEqualTo(product);

        Long originalId = 1L;
        String name2 = "아이폰13";
        int price2 = 300000;
        String status2 = "판매중";
        Product product2 = new Product.Builder()
                .withId(originalId)
                .withName(name2)
                .withPrice(price2)
                .withStatus(status2)
                .build();
        productService.update(product2);
        ProductsResponse products2 = productService.products(0L, 10);
        Assertions.assertThat(products2.getProducts().getFirst()).isEqualTo(product2);
    }

    @Test
    public void 상품1개_상품목록_조회_후_상품삭제_다시조회() {
        ProductService productService = new ProductService(productRepository, productDetailFakeRepository);
        Product product = createProduct();
        productService.add(product, createProductDetail());
        ProductsResponse products = productService.products(0L, 10);
        Assertions.assertThat(products.getProducts().getFirst()).isEqualTo(product);

        productService.delete(0L);
        Assertions.assertThat(products.getProducts().isEmpty()).isEqualTo(true);
    }

    @Test
    public void 상품2개_상품목록_조회_후_상품삭제_다시조회() {
        ProductService productService = new ProductService(productRepository, productDetailFakeRepository);
        Product product = createProduct();
        productService.add(product, createProductDetail());

        Long id2 = 2L;
        String name2 = "아이폰13";
        int price2 = 300000;
        String status2 = "판매중";
        Product product2 = new Product.Builder()
                .withId(id2)
                .withName(name2)
                .withPrice(price2)
                .withStatus(status2)
                .build();
        productService.add(product2, createProductDetail());

        ProductsResponse products = productService.products(0L, 10);

        Assertions.assertThat(products.getProducts().getFirst()).isEqualTo(product);
        Assertions.assertThat(products.getProducts().get(1)).isEqualTo(product2);

        productService.delete(0L);
        Assertions.assertThat(products.getProducts().getFirst()).isEqualTo(product2);
    }

    @Test
    public void 페이지네이션_cursor_limit_검증() {
        // 테스트 데이터를 생성해놓은 paginatedProductFakeRepository 사용
        ProductService productService = new ProductService(paginatedProductFakeRepository, productDetailFakeRepository);

        // cursorId 0L, limit 1 일 때 product id가 1인 것이 반환되어야 함
        ProductsResponse products = productService.products(0L, 1);
        Assertions.assertThat(products.getProducts().getFirst().getId()).isEqualTo(1);

        // cursorId 1L, limit 1 일 때 product id가 2인 것이 반환되어야 함
        products = productService.products(1L, 1);
        Assertions.assertThat(products.getProducts().getFirst().getId()).isEqualTo(2);

        // cursorId 0L, limit 100 일 때 product id가 100인 것이 반환되어야 함
        products = productService.products(0L, 100);
        Assertions.assertThat(products.getCursorId()).isEqualTo(100);

        // cursorId 0L, limit 101 일 때 limit에 사용가능한 max값이 100이기 때문에 product id가 100인 것이 반환되어야 함
        products = productService.products(0L, 101);
        Assertions.assertThat(products.getCursorId()).isEqualTo(100);

        // cursorId 0L, limit -10 일 때 limit에 사용가능한 min값이 0이기 때문에 빈 결과가 반환돼야 함
        products = productService.products(0L, -10);
        Assertions.assertThat(products.getCursorId()).isEqualTo(1);

        // cursorId 0L, limit 1 일 때 다음 커서 id는 1이어야 함
        products = productService.products(0L, 1);
        Assertions.assertThat(products.getCursorId()).isEqualTo(1);

        // cursorId 0L, limit 1 일 때 다음 커서 id는 2여야 함
        products = productService.products(1L, 1);
        Assertions.assertThat(products.getCursorId()).isEqualTo(2);
    }
}
