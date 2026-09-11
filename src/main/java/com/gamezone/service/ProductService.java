package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.persistence.ProductRepository;

import java.util.List;
/**
 * Provides business logic for managing products and inventory.
 */
public class ProductService {

    private ProductRepository repository;
    private List<Product> inventory;
    /**
     * Creates a product service and loads the existing inventory from the repository.
     *
     * @param repository repository used to persist and retrieve products
     */
    public ProductService(ProductRepository repository){
        this.repository = repository;
        this.inventory = repository.findAll();
    }
    /**
     * Registers a new product in the inventory.
     *
     * @param product product to register
     * @throws IllegalArgumentException if a product with the same ID already exists
     */
    public void registerProduct(Product product){
        if (findProductById(product.getId()) != null){
            throw new IllegalArgumentException("Product with id " + product.getId() + " already exists");
        }
        inventory.add(product);
        repository.saveAll(inventory);
    }
    /**
     * Finds a product in the inventory by its ID.
     *
     * @param id ID of the product to find
     * @return the product with the given ID, or null if it does not exist
     */
    public Product findProductById(String id){
        for (Product product : inventory){
            if (product.getId().equals(id)){
                return product;
            }
        }
        return null;
    }

    /**
     * Returns all products currently registered in the inventory.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts(){
        return inventory;
    }
    /**
     * Updates the stock quantity of a product.
     *
     * @param productID ID of the product to update
     * @param newStock new stock quantity
     * @throws IllegalArgumentException if the stock is negative or the product does not exist
     */
    public void updateStock(String productID, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        Product product = findProductById(productID);
        if (product == null) {
            throw new IllegalArgumentException("Product with id " + productID + " does not exist");
        }
        product.setStockQuantity(newStock);
        repository.saveAll(inventory);

    }
}