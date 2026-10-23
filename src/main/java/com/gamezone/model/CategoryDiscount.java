package com.gamezone.model;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Applies a percentage discount to products in a supported category.
 */


public class CategoryDiscount extends Promotion{
    /**
     * new attributes than react to the objects in the sell and calculate the discount
     */
    private double percentageDiscount;
    private String objectCategory;
    private Class<?> targetType;
    private double percentage;

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
    /**
     * Creates a category promotion for video games, consoles, or accessories.
     *
     * @param id promotion identifier
     * @param name promotion name
     * @param startDate first active date
     * @param endDate last active date
     * @param percentageDiscount configured discount rate
     * @param objectCategory target category
     * @param targetType retained for compatibility; the category determines the target type
     * @param percentage configured discount rate
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                            double percentageDiscount, String objectCategory,
                            Class<?> targetType, double percentage) {
        super(id, name, startDate, endDate);
        this.objectCategory = normalizeCategory(objectCategory);
        this.percentage = percentage != 0.0 ? percentage : percentageDiscount;
        this.percentageDiscount = this.percentage;
        this.targetType = typeForCategory(this.objectCategory);
    }

    /**
     * Returns the configured discount rate as a percentage.
     * @return configured discount rate
     */
    public double getPercentageDiscount() {return percentageDiscount;}

    /**
     * Updates the configured discount rate.
     * @param percentageDiscount new discount rate
     */
    public void setPercentageDiscount(double percentageDiscount) {
        this.percentageDiscount = percentageDiscount;
        this.percentage = percentageDiscount;
    }

    /**
     * Returns the normalized target category.
     * @return target category
     */
    public String getObjectCategory() {return objectCategory;}

    /**
     * Updates the target category and its corresponding product type.
     * @param objectCategory new target category
     */
    public void setObjectCategory(String objectCategory) {
        this.objectCategory = normalizeCategory(objectCategory);
        this.targetType = typeForCategory(this.objectCategory);
    }
    /**
     * Returns the configured discount percentage.
     * @return configured percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Returns the product class targeted by this promotion.
     * @return target product class
     */
    public Class<?> getTargetType() {
        return targetType;
    }

    private static String normalizeCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Promotion category cannot be null.");
        }
        String normalized = category.trim().toUpperCase(Locale.ROOT);
        if (!normalized.equals("VIDEOGAME") && !normalized.equals("CONSOLE")
                && !normalized.equals("ACCESSORY")) {
            throw new IllegalArgumentException("Unsupported promotion category: " + category);
        }
        return normalized;
    }

    private static Class<?> typeForCategory(String category) {
        return switch (category) {
            case "VIDEOGAME" -> VideoGame.class;
            case "CONSOLE" -> Console.class;
            case "ACCESSORY" -> Accessory.class;
            default -> throw new IllegalArgumentException("Unsupported promotion category: " + category);
        };
    }


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
            boolean matches = product != null && ("ACCESSORY".equals(objectCategory)
                    ? product instanceof Accessory : targetType.isInstance(product));
            if (matches) {
                eligibleSubtotal += product.getPrice();
            }
        }

        return eligibleSubtotal * percentage / 100.0;
    }

}
