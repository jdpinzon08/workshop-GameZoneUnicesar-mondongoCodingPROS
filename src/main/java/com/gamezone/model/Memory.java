package com.gamezone.model;

import java.util.List;

/**
 * Represents Memory accessories.
 */
public class Memory extends Accessory {

    private int capacityInGB;
    private String memoryType; // example: "MicroSD", "NVMe SSD"

    public Memory(String id, String title, double price, int stockQuantity, List<String> compatibleConsoles, int capacityInGB, String memoryType) {
        super(id, title, price, stockQuantity, compatibleConsoles);
        this.capacityInGB = capacityInGB;
        this.memoryType = memoryType;
    }

    public int getCapacityInGB() {
        return this.capacityInGB;
    }

    public void setCapacityInGB(int capacityInGB) {
        this.capacityInGB = capacityInGB;
    }

    public String getMemoryType() {
        return this.memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " | capacity: " + capacityInGB + "GB | Type: " + memoryType;
    }
}