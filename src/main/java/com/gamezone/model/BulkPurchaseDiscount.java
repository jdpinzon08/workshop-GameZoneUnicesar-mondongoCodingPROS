package com.gamezone.model;

import java.time.LocalDate;

public class BulkPurchaseDiscount extends Promotion {


    /**
     * new unique attributes for BulkPurchaseDiscount
     */

    private int minimumQuantity;
    private double discountPercentage;
    //clase para poder hacer del metodo mas polifasetico si se le puede decir de esa manera
    private final Class targetType;
    private final double percentage;


    /**
     * extended attributes and new attributes in the constructor
     * @param id identification of promotion
     * @param name name of promotion
     * @param startDate initial date for promotion
     * @param endDate final date for promotion
     * @param targetType the class of object that the promotion is available
     * @param percentage percentage that will be subtracted
     * @param discountPercentage total with the discount percentage
     * @param minimumQuantity the minimum of objects for be able the discount
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minimumQuantity, double discountPercentage, Class targetType, double percentage) {
        super(id, name, startDate, endDate);
        this.discountPercentage = discountPercentage;
        this.minimumQuantity = minimumQuantity;
        this.targetType = targetType;
        this.percentage = percentage;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    /**
     * The discount is calculated for the purchase of the desired products of the corresponding type and in sufficient quantity.
     * @param sale the sale to which the promotion is applied
     * @return the price without the percentage rested
     */
    @Override
    public double calculateDiscount(Sale sale) {
        int sum = 0;
        double price=0.0;

        for (Product product : sale.getProducts()) {
            if (targetType.isInstance(product)) {
                sum++;
                price+= product.getPrice();
            }
        }
        //asi cuando se use este metodo solo se tiene que restar el total de la venta con este mismo metodo y sale el total neto
        if (sum >= minimumQuantity) {
            discountPercentage = price-((price*percentage)/100);
        }

        return discountPercentage;

    }
}