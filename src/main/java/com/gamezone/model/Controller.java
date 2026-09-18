package com.gamezone.model;

import java.util.List;

/**
 * Represents a Controller for consoles.
 */
public class Controller extends Accessory {

    private String connectionType; // ej: "Wireless" o "Wired"

    public Controller(String id, String title, double price, int stockQuantity, List<String> compatibleConsoles, String connectionType) {
        super(id, title, price, stockQuantity, compatibleConsoles);
        this.connectionType = connectionType;
    }

    public String getConnectionType() {
        return this.connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " | Connection: " + connectionType;
    }
}