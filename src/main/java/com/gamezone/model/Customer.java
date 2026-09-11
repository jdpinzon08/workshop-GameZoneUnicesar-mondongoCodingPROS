package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the GameZone store who purchases products.
 * Inherits common personal attributes from the abstract Person class.
 *
 * @author Desarrollador 2
 * @version 1.0
 */
public class Customer extends Person {

    private String email;
    private List<Sale> purchaseHistory;

    /**
     * Constructs a new Customer with the specified details.
     * Initializes an empty purchase history list.
     *
     * @param id          the unique identification of the customer
     * @param name        the full name of the customer
     * @param phoneNumber the contact phone number of the customer
     * @param email       the email address of the customer
     */
    public Customer(String id, String name, String phoneNumber, String email) {
        super(id, name, phoneNumber);
        this.email = email;
        this.purchaseHistory = new ArrayList<>();
    }

    /**
     * Returns the email address of the customer.
     *
     * @return the email string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the purchase history of the customer.
     *
     * @return a list of sales associated with this customer
     */
    public List<Sale> getPurchaseHistory() {
        return purchaseHistory;
    }

    /**
     * Sets or updates the email address of the customer.
     *
     * @param email the new email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Adds a new sale transaction to the customer's purchase history.
     *
     * @param sale the sale object to add
     */
    public void addSaleToHistory(Sale sale) {
        if (sale != null) {
            this.purchaseHistory.add(sale);
        }
    }
}