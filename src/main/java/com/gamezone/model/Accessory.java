package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a accessory in GameZone.
 */
public abstract class Accessory extends Product {

    private List<String> compatibleConsoles;

    public Accessory(String id, String title, double price, int stockQuantity, List<String> compatibleConsoles) {
        super(id, title, price, stockQuantity);
        this.compatibleConsoles = (compatibleConsoles != null) ? compatibleConsoles : new ArrayList<>();
    }

    public List<String> getCompatibleConsoles() {
        return this.compatibleConsoles;
    }

    public void setCompatibleConsoles(List<String> compatibleConsoles) {
        this.compatibleConsoles = compatibleConsoles;
    }

    @Override
    public String getDescription() {
        return "ID: " + getId() + " | Title: " + getTitle() + " | Price: $" + getPrice() +
                " | Stock: " + getStockQuantity() + " | Compatible Consoles: " + String.join(", ", compatibleConsoles);
    }
}