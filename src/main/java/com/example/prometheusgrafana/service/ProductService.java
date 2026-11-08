package com.example.prometheusgrafana.service;

import com.example.prometheusgrafana.domain.Product;
import com.example.prometheusgrafana.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product save(Product p) {
        return repository.save(p);
    }
}

