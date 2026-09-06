package com.gamezone.model;

public abstract class Product {
    private String id;
    private String title;
    private double price;
    private int stockQuantity;

    public Product(String id, String title, double price, int stockQuantity){
        this.id=id;
        this.title=title;
        this.price=price;
        this.stockQuantity=stockQuantity;
    }
    public String getId(){
        return this.id;
    }
    public String getTitle(){
        return this.title;
    }
    public double getPrice(){
        return this.price;
    }
    public int getStockQuantity(){
        return this.stockQuantity;
    }
    public void setId(String id){
        this.id=id;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setPrice(double price){
        this.price=price;
    }
    public void setStockQuantity(int stockQuantity){
        this.stockQuantity=stockQuantity;
    }
    public abstract String getDescription();

}
