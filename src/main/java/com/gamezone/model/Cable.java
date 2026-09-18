package com.gamezone.model;

import java.util.List;

/**
 * Represents a Cable accessory
 */
public class Cable extends Accessory {

    private double lengthInMeters;
    private String connectorType; // ej: "HDMI", "USB-C"

    public Cable(String id, String title, double price, int stockQuantity, List<String> compatibleConsoles, double lengthInMeters, String connectorType) {
        super(id, title, price, stockQuantity, compatibleConsoles);
        this.lengthInMeters = lengthInMeters;
        this.connectorType = connectorType;
    }

    public double getLengthInMeters() {
        return this.lengthInMeters;
    }

    public void setLengthInMeters(double lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    public String getConnectorType() {
        return this.connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " | Length: " + lengthInMeters + "m | Connector: " + connectorType;
    }
}