package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Person {

    private String email;
    private List<Sale> purchaseHistory;

    public Customer(String id, String name, String email, String phoneNumber) {
        super(id, name, phoneNumber);
        this.email = email;
        this.purchaseHistory = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Sale> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void addSaleToHistory(Sale sale) {
        if (sale != null) {
            this.purchaseHistory.add(sale);
        }
    }
}
