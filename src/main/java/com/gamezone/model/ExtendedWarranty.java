package com.gamezone.model;
import java.time.LocalDate;

public class ExtendedWarranty extends Warranty {
    /**
     * Constructs an ExtendedWarranty instance.
     *
     * @param id the unique identifier for the extended warranty
     * @param product the product associated with the extended warranty
     * @param sale the sale associated with the extended warranty
     * @param startDate the start date of the extended warranty
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super (id, product, sale, startDate);
    }
    @Override
    public int getDurationInMonths(){
        return 12;
    }
    @Override
    public String getWarrantyType(){
        return "Extended Warranty";
    }
    @Override
    public double getAdditionalCost(){
        if (getProduct() != null){
            return getProduct().getPrice() * 0.10;
        } return 0.0;
    }
}
