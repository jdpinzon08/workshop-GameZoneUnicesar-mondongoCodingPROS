package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Product;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.validation.ProductValidator;

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
     * @throws InvalidDataException if the product or one of its required fields is invalid
     * @throws BusinessRuleException if a product with the same ID already exists
     */
    public void registerProduct(Product product){
        if (product == null) {
            throw new InvalidDataException("product", "no puede ser nulo");
        }
        ProductValidator.validateProductData(product.getId(), product.getTitle(),
                product.getPrice(), product.getStockQuantity());
        if (findProductById(product.getId()) != null){
            throw new BusinessRuleException("Ya existe un producto con identificador " + product.getId());
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
     * @param productId ID of the product to update
     * @param newStock new stock quantity
     * @throws InvalidDataException if the new stock is negative
     * @throws com.gamezone.exceptions.ResourceNotFoundException if the product does not exist
     */
    public void updateStock(String productId, int newStock) {
        Product product = findProductById(productId);
        ProductValidator.validateProductExists(product, productId);
        ProductValidator.validateProductData(product.getId(), product.getTitle(),
                product.getPrice(), newStock);
        product.setStockQuantity(newStock);
        repository.saveAll(inventory);
    }
    /** Adds the returned quantity back to an inventory product. */
    public void restoreStock(String productId, int quantity) {
        Product product = findProductById(productId);
        if (product != null) {
            updateStock(productId, product.getStockQuantity() + quantity);
        }
    }
}
