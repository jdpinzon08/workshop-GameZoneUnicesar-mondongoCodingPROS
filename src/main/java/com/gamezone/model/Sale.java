package com.gamezone.model;
import java.util.List;
import java.util.ArrayList;


public class Sale {

private String saleId;
private List<Product> products;
private double totalAmount;


public Sale(String saleId, List<Product> products, double totalAmount){
    this.products=products;
    this.saleId=saleId;
    this.totalAmount=totalAmount;
}

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }



}
