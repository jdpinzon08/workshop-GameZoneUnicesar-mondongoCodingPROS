package com.gamezone.model;

import java.time.LocalDate;

/**
 * Abstract class for promotions
 */
public abstract class Promotion {
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    /**
     * Creates a promotion with its basic information and validity period.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate the start date
     * @param endDate the end date
     */
    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    /**
     * Gets the promotion identifier.
     *
     * @return the promotion identifier
     */
    public String getId() {
        return id;
    }
    /**
     * Sets the promotion identifier.
     *
     * @param id the new promotion identifier
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * Gets the promotion name.
     *
     * @return the promotion name
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the promotion name.
     *
     * @param name the new promotion name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Gets the promotion start date.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
    /**
     * Sets the promotion start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    /**
     * Gets the promotion end date.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }
    /**
     * Sets the promotion end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    /**
     * Checks whether the promotion is active on a given date.
     *
     * @param date the date to check
     * @return true if the promotion is active; false otherwise
     */
    public boolean isActive(LocalDate date){
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    /**
     * Calculates the discount applied to a sale.
     *
     * @param sale the sale to which the promotion is applied
     * @return the calculated discount
     */
    public abstract double calculateDiscount(Sale sale);
}

//AI has not been used in this code.