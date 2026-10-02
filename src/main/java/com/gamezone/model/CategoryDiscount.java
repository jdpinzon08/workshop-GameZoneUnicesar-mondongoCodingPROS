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


    //new methods

    /**
     * will calculate the discount and iterating on the sale searching for products of the type
     * @param sale the sale to which the promotion is applied
     * @return the price without the percentage rested
     */
    @Override
    public double calculateDiscount(Sale sale) {
        percentageDiscount=0.0;
        double sum = 0.0;

        for (Product product:sale.getProducts()) {
            if(targetType.isInstance(product)) {
                sum+= product.getPrice();
            }
        }
        if(sum!=0.0){
            percentageDiscount=sum-((sum*percentage)/100);
        }

        return percentageDiscount;
    }
}
