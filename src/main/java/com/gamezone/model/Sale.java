package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/** Represents a completed sale and its customer, seller, products, and date. */
public class Sale {

    private String saleId;
    private List<Product> products;
    private double totalAmount;
    private String date;
    private Seller seller;
    private Customer customer;
    private LocalDate saleDate;
    private String appliedPromotionName;
    private double discountAmount;

    /** Creates a sale with its recorded total and associated people and products. */
    public Sale(String saleId, List<Product> products, double totalAmount, String date, Customer customer, Seller seller, String appliedPromotionName, double discountAmount) {
        this.saleId = saleId;
        this.products = (products != null) ? products : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.date = date;
        this.appliedPromotionName=appliedPromotionName;
        this.discountAmount=discountAmount;

        if (date != null && !date.trim().isEmpty()) {
            this.saleDate = LocalDate.parse(date);
        }
        this.seller = seller;
        this.customer = customer;
    }

    /** Returns the sum of the prices of the sale's non-null products. */
    public double calculateTotal() {
        double sum = 0.0;
        if (products != null) {
            for (Product product : products) {
                if (product != null) {
                    sum += product.getPrice();
                }
            }
        }
        return sum;
    }

    public String getSaleId() { return saleId; }
    public List<Product> getProducts() { return products; }
    public double getTotalAmount() { return totalAmount; }
    /** Sets the sale total, including any additional charges. */
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount;}
    public String getDate() { return date; }
    /** Returns the parsed sale date, or null when the date is unavailable. */
    public LocalDate getSaleDate() { return saleDate; }
    public Seller getSeller() { return seller; }
    public Customer getCustomer() { return customer; }
    public String getAppliedPromotionName() {return appliedPromotionName;}
    public void setAppliedPromotionName(String appliedPromotionName) {this.appliedPromotionName = appliedPromotionName;}
    public double getDiscountAmount() {return discountAmount;}
    public void setDiscountAmount(double discountAmount) {this.discountAmount = discountAmount;}


    /**
     * Checks if the sale is eligible for a return within the allowed 30-day window.
     *
     * @return true if the current date is within 30 calendar days after the sale date; false otherwise.
     */
    public boolean canBeReturned() {
        if (this.saleDate == null) {
            return false;
        }
        LocalDate currentDate = LocalDate.now();
        return !currentDate.isAfter(this.saleDate.plusDays(30));
    }
}
