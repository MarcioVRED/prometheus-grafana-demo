package com.example.prometheusgrafana.controller;

import com.example.prometheusgrafana.domain.Product;
import com.example.prometheusgrafana.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product saved = service.save(product);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId())).body(saved);
    }
}

