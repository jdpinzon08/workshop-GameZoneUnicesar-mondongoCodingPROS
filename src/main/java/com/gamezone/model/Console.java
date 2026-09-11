package com.gamezone.model;

/**
 * Represents a console product in the GameZone inventory.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    /**
     * Creates a new console.
     *
     * @param id unique identifier of the console
     * @param title title of the console
     * @param price price of the console
     * @param stockQuantity available quantity in stock
     * @param brand brand of the console
     * @param model model of the console
     * @param generation generation of the console
     */
    public Console (String id, String title, double price, int stockQuantity, String brand, String model, String generation) {
        super (id, title, price, stockQuantity);

        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    /**
     * Returns the brand of the console.
     *
     * @return console brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Updates the console brand.
     *
     * @param brand new console brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Returns the model of the console.
     *
     * @return console model
     */
    public String getModel() {
        return model;
    }

    /**
     * Updates the console model.
     *
     * @param model new console model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the generation of the console.
     *
     * @return console generation
     */
    public String getGeneration() {
        return generation;
    }

    /**
     * Updates the console generation.
     *
     * @param generation new console generation
     */
    public void setGeneration(String generation) {
        this.generation = generation;
    }

    /**
     * Returns a description of the console.
     *
     * @return console description
     */
    @Override
    public String getDescription() {
        return getTitle() + " - " + brand + " - " + model + " - " + generation;
    }
}