package com.gamezone.model;

import java.time.LocalDate;

/**
 * Abstract class representing a warranty associated with a product sale.
 */
public abstract class Warranty {
    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Constructs a Warranty instance and calculates its end date based on duration.
     *
     * @param id        the unique identifier of the warranty
     * @param product   the product associated with the warranty
     * @param sale      the sale associated with the warranty
     * @param startDate the start date of the warranty
     */
    public Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Sale getSale() {
        return sale;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Gets the duration of the warranty in months.
     *
     * @return duration in months
     */
    public abstract int getDurationInMonths();

    /**
     * Gets the type name of the warranty.
     *
     * @return the warranty type name
     */
    public abstract String getWarrantyType();

    /**
     * Gets the additional cost added by the warranty.
     *
     * @return additional cost
     */
    public abstract double getAdditionalCost();

    /**
     * Checks if the warranty is active on the given date.
     *
     * @param date the date to check
     * @return true if the date is between start and end date inclusive, false otherwise
     */
    public boolean isActive(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Generates a formatted certificate of the warranty in Spanish.
     *
     * @return formatted certificate string
     */
    public String generateWarrantyCertificate() {
        return String.format(
                "=== CERTIFICADO DE GARANTÍA ===\n" +
                        "ID Garantía: %s\n" +
                        "Tipo: %s\n" +
                        "Producto: %s\n" +
                        "Cliente: %s\n" +
                        "Fecha Inicio: %s\n" +
                        "Fecha Vencimiento: %s\n" +
                        "Costo Adicional: $%.2f\n" +
                        "===============================",
                id,
                getWarrantyType(),
                product != null ? product.getTitle() : "N/A",
                sale != null && sale.getCustomer() != null ? sale.getCustomer().getName() : "N/A",
                startDate,
                endDate,
                getAdditionalCost()
        );
    }
}