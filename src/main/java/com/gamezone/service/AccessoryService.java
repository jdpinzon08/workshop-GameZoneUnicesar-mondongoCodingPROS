package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
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

    /**
     * Constructs AccessoryService with the injected AccessoryRepository.
     *
     * @param repository The repository handling persistence.
     */
    public AccessoryService(AccessoryRepository repository) {
        this.repository = repository;
        this.accessories = repository.loadAll();
    }

    /**
     * Registers a new controller accessory.
     */
    public void registerController(String id, String title, double price, int stock, List<String> compatibleConsoles, String connectionType) {
        Controller controller = new Controller(id, title, price, stock, compatibleConsoles, connectionType);
        accessories.add(controller);
        repository.saveAll(accessories);
    }

    /**
     * Registers a new cable accessory.
     */
    public void registerCable(String id, String title, double price, int stock, List<String> compatibleConsoles, double length, String connectorType) {
        Cable cable = new Cable(id, title, price, stock, compatibleConsoles, length, connectorType);
        accessories.add(cable);
        repository.saveAll(accessories);
    }

    /**
     * Registers a new memory accessory.
     */
    public void registerMemory(String id, String title, double price, int stock, List<String> compatibleConsoles, int capacity, String memoryType) {
        Memory memory = new Memory(id, title, price, stock, compatibleConsoles, capacity, memoryType);
        accessories.add(memory);
        repository.saveAll(accessories);
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
     * @param accessoryId The ID of the accessory.
     * @param quantity The new stock quantity.
     * @throws IllegalArgumentException if the accessory is not found.
     */
    public void updateStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        if (accessory != null) {
            accessory.setStockQuantity(quantity);
            repository.saveAll(accessories);
        } else {
            throw new IllegalArgumentException("Accessory not found with ID: " + accessoryId);
        }
    }
}