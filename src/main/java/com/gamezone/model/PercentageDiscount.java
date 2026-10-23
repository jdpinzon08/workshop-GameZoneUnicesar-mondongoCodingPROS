package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a promotion with a percentage discount.
 */
public class PercentageDiscount extends Promotion{
    private double percentage;

    /**
     * creates a PercentageDiscount promotion.
     * @param id the promotion identifier.
     * @param name the promotion name.
     * @param startDate the promotion start date.
     * @param endDate the promotion end date.
     * @param percentage the percentage discount.
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
    }

    /**
     * gets the percentage discount.
     * @return the percentage discount.
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * sets the percentage discount.
     * @param percentage the percentage discount.
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * calculates the discount for a given sale.
     * @param sale the sale to which the promotion is applied
     * @return the discount amount
     */
    @Override
    public double calculateDiscount(Sale sale) {
        return sale.getTotalAmount() * (percentage / 100);
    }
}
