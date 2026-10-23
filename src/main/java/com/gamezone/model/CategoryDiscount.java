package com.gamezone.model;

import java.time.LocalDate;

/**
 * A class that applies discounts based on the category of the item being purchased—"video game" or "console."
 */


public class CategoryDiscount extends Promotion{
    /**
     * new attributes than react to the objects in the sell and calculate the discount
     */
    private double percentageDiscount;
    private String objectCategory;
    private final Class targetType;
    private final double percentage;

    /**
     * extended attributes and new attributes in the constructor
     * @param id identification of promotion
     * @param name name of promotion
     * @param startDate initial date for promotion
     * @param endDate final date for promotion
     * @param percentageDiscount percentage of discount of the promotion
     * @param objectCategory type of object of the promotion
     * @param targetType the class of object
     * @param percentage the percentage that will be used for the rest
     */
    public CategoryDiscount(String id, String name, LocalDate startDate,LocalDate endDate, double percentageDiscount, String objectCategory,Class targetType,double percentage){
        super(id, name, startDate, endDate);
        this.objectCategory=objectCategory;
        this.percentageDiscount=percentageDiscount;
        this.targetType=targetType;
        this.percentage=percentage;
    }

    /**
     * getters and setter of the new attributes
     *
     */
    public double getPercentageDiscount() {return percentageDiscount;}

    public void setPercentageDiscount(double percentageDiscount) {this.percentageDiscount = percentageDiscount;}

    public String getObjectCategory() {return objectCategory;}

    public void setObjectCategory(String objectCategory) {this.objectCategory = objectCategory;}
    /**
     * Returns the configured discount percentage.
     *
     * @return the configured discount percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Returns the class targeted by this promotion.
     *
     * @return the target product class
     */
    public Class getTargetType() {
        return targetType;
    }


    //new methods

    /**
     * Calculates the discount for products matching the configured target type.
     *
     * @param sale the sale to which the promotion is applied
     * @return the calculated discount amount
     */
    @Override
    public double calculateDiscount(Sale sale) {
        if (sale == null) {
            return 0.0;
        }

        double eligibleSubtotal = 0.0;

        for (Product product : sale.getProducts()) {
            if (product != null && targetType.isInstance(product)) {
                eligibleSubtotal += product.getPrice();
            }
        }

        percentageDiscount = eligibleSubtotal * percentage / 100.0;
        return percentageDiscount;
    }

}
