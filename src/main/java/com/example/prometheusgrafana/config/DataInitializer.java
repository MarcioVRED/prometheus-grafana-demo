package com.example.prometheusgrafana.config;

import com.example.prometheusgrafana.domain.Product;
import com.example.prometheusgrafana.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ProductRepository repository;

    public DataInitializer(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            List<Product> products = List.of(
                    Product.builder().name("Caneta Bic").price(1.99).build(),
                    Product.builder().name("Caderno 100 folhas").price(12.50).build(),
                    Product.builder().name("Mochila Escolar").price(89.90).build(),
                    Product.builder().name("Teclado Mecânico").price(199.00).build(),
                    Product.builder().name("Mouse Óptico").price(49.90).build()
            );
            repository.saveAll(products);
            log.info("Inicializado H2 com {} produtos", products.size());
        } else {
            log.info("H2 já possui {} produtos", repository.count());
        }
    }
}

