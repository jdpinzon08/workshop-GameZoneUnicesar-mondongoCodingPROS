package com.gamezone.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Warranty {
    private double price;
    private String warrantyId;
    private Product associatedProduct;
    private Sale associatedSale;
    private LocalDate startDate;
    private LocalDate endDate;

    public Warranty(String warrantyId, Product associatedProduct, Sale associatedSale, LocalDate startDate, LocalDate endDate) {
        this.warrantyId = warrantyId;
        this.associatedProduct = associatedProduct;
        this.associatedSale = associatedSale;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getWarrantyId() {

        return warrantyId;
    }

    public Product getAssociatedProduct() {

        return associatedProduct;
    }

    public Sale getAssociatedSale()
    {
        return associatedSale;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDurationInMonths(){
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }

    public String getWarrantyType(){
        return "your warranty is: ";
    }

    public double getAdditionalCost(){
        return 0;
    };

    public boolean isActive(LocalDate date){
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    public String generateWarrantyCertificate(){
        return "Warranty Certificate";
    }
}
