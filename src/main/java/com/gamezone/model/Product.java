package com.gamezone.model;

/**
 * Represents the base product in the GameZone inventory.
 * This class is abstract and is extended by specific product types.
 */
public abstract class Product {

    private String id;
    private String title;
    private double price;
    private int stockQuantity;

    /**
     * Creates a new product.
     *
     * @param id unique identifier of the product
     * @param title title of the product
     * @param price price of the product
     * @param stockQuantity available quantity in stock
     */
    public Product(String id, String title, double price, int stockQuantity){
        this.id = id;
        this.title = title;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Returns the product ID.
     *
     * @return product ID
     */
    public String getId(){
        return this.id;
    }

    /**
     * Returns the product title.
     *
     * @return product title
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Returns the product price.
     *
     * @return product price
     */
    public double getPrice(){
        return this.price;
    }

    /**
     * Returns the current stock quantity.
     *
     * @return stock quantity
     */
    public int getStockQuantity(){
        return this.stockQuantity;
    }

    /**
     * Updates the product ID.
     *
     * @param id new product ID
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Updates the product title.
     *
     * @param title new product title
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * Updates the product price.
     *
     * @param price new product price
     */
    public void setPrice(double price){
        this.price = price;
    }

    /**
     * Updates the stock quantity.
     *
     * @param stockQuantity new stock quantity
     */
    public void setStockQuantity(int stockQuantity){
        this.stockQuantity = stockQuantity;
    }

    /**
     * Returns a description of the product.
     *
     * @return product description
     */
    public abstract String getDescription();
}