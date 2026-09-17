package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Sale {

    private String saleId;
    private List<Product> products;
    private double totalAmount;
    private String date;
    private Seller seller;
    private Customer customer;
    private LocalDate saleDate;

    public Sale(String saleId, List<Product> products, double totalAmount, String date, Customer customer, Seller seller) {
        this.saleId = saleId;
        this.products = (products != null) ? products : new ArrayList<>();
        this.totalAmount = calculateTotal();
        this.date = date;
        // Parseamos el String date a LocalDate para no tener NullPointerException
        if (date != null && !date.trim().isEmpty()) {
            this.saleDate = LocalDate.parse(date);
        }
        this.seller = seller;
        this.customer = customer;
    }

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
    public String getDate() { return date; }
    public LocalDate getSaleDate() { return saleDate; }
    public Seller getSeller() { return seller; }
    public Customer getCustomer() { return customer; }

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