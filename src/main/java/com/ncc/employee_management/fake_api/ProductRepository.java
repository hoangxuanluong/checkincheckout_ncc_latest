package com.ncc.employee_management.fake_api;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final RestTemplate restTemplate;

    @Value("${fake-api.store.url}")
    private String url;

    public List<Product> findAllProducts() {
        List<Product> products = List.of(restTemplate.getForObject(url, Product[].class));
        return products;
    }
}
