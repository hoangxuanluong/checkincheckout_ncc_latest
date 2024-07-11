package com.ncc.employee_management.fake_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {


    private final ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        log.info("GET::LIST PRODUCTS");
        return productRepository.findAllProducts();

    }
}
