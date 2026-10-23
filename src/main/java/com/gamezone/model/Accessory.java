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
        this.compatibleConsoles = compatibleConsoles == null
                ? new ArrayList<>() : new ArrayList<>(compatibleConsoles);
    }

    /** Returns a copy of the console identifiers compatible with this accessory. */
    public List<String> getCompatibleConsoles() {
        return new ArrayList<>(this.compatibleConsoles);
    }

    /** Replaces the compatible console identifiers with a defensive copy. */
    public void setCompatibleConsoles(List<String> compatibleConsoles) {
        this.compatibleConsoles = compatibleConsoles == null
                ? new ArrayList<>() : new ArrayList<>(compatibleConsoles);
    }

    @Override
    public String getDescription() {
        return "ID: " + getId() + " | Title: " + getTitle() + " | Price: $" + getPrice() +
                " | Stock: " + getStockQuantity() + " | Compatible Consoles: " + String.join(", ", compatibleConsoles);
    }
}
