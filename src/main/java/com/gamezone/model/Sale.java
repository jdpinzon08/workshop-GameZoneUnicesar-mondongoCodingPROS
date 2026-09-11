package com.gamezone.model;
import java.util.List;
import java.util.ArrayList;



public class Sale {



private String saleId;
private List<Product> products;
private double totalAmount;
private String date;
private Seller seller;
private Customer customer;


public Sale(String saleId, List<Product> products, double totalAmount, String date, Customer customer, Seller seller){
    this.saleId=saleId;
    this.totalAmount=calculateTotal();
    //esto es pa que se verifique que la lista de productos no este vacia
    this.products = (products != null) ? products : new ArrayList<>();
    this.date=date;
    this.seller=seller;
    this.customer=customer;

}
//calculo de el total de la compra de un cliente
    public double calculateTotal(){
    double sum= 0.0;
    if(products!=null){
        for(Product product : products){
            if(product!=null){
                sum += product.getPrice();
            }
        }
    }
//a
    return sum;
    }
    //puro get nomas porque no seria bueno setters aqui, asi se podria modificar y no aguanta
    public String getSaleId() {return saleId;}
    public List<Product> getProducts() {return products;}
    public double getTotalAmount() {return totalAmount;}
    public String getDate(){return date;}
    public Seller getSeller(){return seller;}
    public Customer getCustomer(){return customer;}




}
