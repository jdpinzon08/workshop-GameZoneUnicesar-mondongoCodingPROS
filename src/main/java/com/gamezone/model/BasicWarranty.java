package com.gamezone.model;

import java.time.LocalDate;

/**
 * Concrete class representing a basic factory warranty.
 * Covers factory defects for 6 months at no extra cost.
 */
public class BasicWarranty extends Warranty {

    /**
     * Constructs a BasicWarranty instance.
     *
     * @param id        the unique identifier of the warranty
     * @param product   the product associated with the warranty
     * @param sale      the sale associated with the warranty
     * @param startDate the start date of the warranty
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return 6;
    }

    @Override
    public String getWarrantyType() {
        return "Basic Warranty";
    }

    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}