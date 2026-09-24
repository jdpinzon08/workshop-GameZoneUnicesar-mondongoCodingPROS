package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Persists accessories as products in the shared product CSV. */
public class AccessoryRepository {

    private final ProductRepository productRepository;

    public AccessoryRepository() {
        this(new ProductRepository());
    }

    public AccessoryRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void saveAll(List<Accessory> accessories) {
        List<Product> products = productRepository.findAll().stream()
                .filter(product -> !(product instanceof Accessory))
                .collect(Collectors.toCollection(ArrayList::new));
        products.addAll(accessories);
        productRepository.saveAll(products);
    }

    public List<Accessory> loadAll() {
        return productRepository.findAll().stream()
                .filter(Accessory.class::isInstance)
                .map(Accessory.class::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
