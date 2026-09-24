package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.model.Product;
import com.gamezone.persistence.AccessoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class managing business logic and operations for accessories.
 */
public class AccessoryService {

    private final AccessoryRepository repository;
    private final List<Accessory> accessories;
    private final ProductService productService;

    /**
     * Constructs AccessoryService with the injected AccessoryRepository.
     *
     * @param repository The repository handling persistence.
     */
    public AccessoryService(AccessoryRepository repository) {
        this(repository, null);
    }

    /** Loads accessories from the shared product service when available. */
    public AccessoryService(AccessoryRepository repository, ProductService productService) {
        this.repository = repository;
        this.productService = productService;
        if (productService == null) {
            this.accessories = repository.loadAll();
        } else {
            this.accessories = productService.getAllProducts().stream()
                    .filter(Accessory.class::isInstance)
                    .map(Accessory.class::cast)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    /**
     * Registers a new controller accessory.
     */
    public void registerController(String id, String title, double price, int stockQuantity, List<String> compatibleConsoles, String connectionType) {
        Controller controller = new Controller(id, title, price, stockQuantity, compatibleConsoles, connectionType);
        registerAccessory(controller);
    }

    /**
     * Registers a new cable accessory.
     */
    public void registerCable(String id, String title, double price, int stockQuantity, List<String> compatibleConsoles, double lengthInMeters, String connectorType) {
        Cable cable = new Cable(id, title, price, stockQuantity, compatibleConsoles, lengthInMeters, connectorType);
        registerAccessory(cable);
    }

    /**
     * Registers a new memory accessory.
     */
    public void registerMemory(String id, String title, double price, int stockQuantity, List<String> compatibleConsoles, int capacityInGB, String memoryType) {
        Memory memory = new Memory(id, title, price, stockQuantity, compatibleConsoles, capacityInGB, memoryType);
        registerAccessory(memory);
    }

    /**
     * Returns all registered accessories.
     *
     * @return List of all accessories.
     */
    public List<Accessory> listAllAccessories() {
        return new ArrayList<>(accessories);
    }

    /**
     * Filters accessories by their simple class type name.
     *
     * @param type Simple class name to filter by (Controller, Cable, Memory).
     * @return Filtered list of accessories.
     */
    public List<Accessory> listAccessoriesByType(String type) {
        return accessories.stream()
                .filter(a -> a.getClass().getSimpleName().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Finds accessories compatible with a specified console ID.
     *
     * @param consoleId The ID of the console.
     * @return List of compatible accessories.
     */
    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
        return accessories.stream()
                .filter(a -> a.getCompatibleConsoles() != null && a.getCompatibleConsoles().contains(consoleId))
                .collect(Collectors.toList());
    }

    /**
     * Finds an accessory by its unique identifier.
     *
     * @param id The accessory ID.
     * @return Found Accessory, or null if not found.
     */
    public Accessory findById(String id) {
        return accessories.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates the stock quantity for a given accessory.
     *
     * @param productId The ID of the accessory product.
     * @param newStock The new stock quantity.
     * @throws IllegalArgumentException if the accessory is not found.
     */
    public void updateStock(String productId, int newStock) {
        Accessory accessory = findById(productId);
        if (accessory != null) {
            if (productService != null) {
                productService.updateStock(productId, newStock);
                accessory.setStockQuantity(newStock);
            } else {
                accessory.setStockQuantity(newStock);
            }
            repository.saveAll(accessories);
        } else {
            throw new IllegalArgumentException("Accessory not found with ID: " + productId);
        }
    }

    private void registerAccessory(Accessory accessory) {
        if (productService != null) {
            Product existingProduct = productService.findProductById(accessory.getId());
            if (existingProduct != null) {
                throw new IllegalArgumentException("Product with id " + accessory.getId() + " already exists");
            }
            productService.registerProduct(accessory);
        }
        accessories.add(accessory);
        repository.saveAll(accessories);
    }
}
