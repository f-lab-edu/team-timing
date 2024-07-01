package com.github.thundermarket.thundermarket.controller;

import com.github.thundermarket.thundermarket.domain.*;
import com.github.thundermarket.thundermarket.service.ProductDetailService;
import com.github.thundermarket.thundermarket.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;
    private final ProductDetailService productDetailService;

    public ProductController(ProductService productService, ProductDetailService productDetailService) {
        this.productService = productService;
        this.productDetailService = productDetailService;
    }

    @GetMapping("/api/v1/products")
    public ResponseEntity<ProductsResponse> products(
            @RequestParam(name = "cursorId", defaultValue = "0") Long cursorId,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        return new ResponseEntity<>(productService.products(cursorId, limit), HttpStatus.OK);
    }

    @GetMapping("/api/v1/products/{id}")
    public ResponseEntity<ProductDetailResponse> productDetail(@PathVariable("id") Long id) {
        return new ResponseEntity<>(productDetailService.productDetail(id), HttpStatus.OK);
    }

    @PostMapping("/api/v1/products")
    public ResponseEntity<ProductResponse> add(@RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.add(productRequest.toProduct(), productRequest.toProductDetail()), HttpStatus.OK);
    }
}
