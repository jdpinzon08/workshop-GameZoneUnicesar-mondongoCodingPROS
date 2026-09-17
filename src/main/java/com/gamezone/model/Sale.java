package com.gamezone.model;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a sales transaction within the GameZone system.
 * Stores details regarding the sale ID, list of purchased products,
 * total transaction amount, date, customer, and selling staff member.
 *
 * @author Technical Lead (mondongoCodingPROS)
 * @version 1.0
 */

public class Sale {

private String saleId;
private List<Product> products;
private double totalAmount;
private String date;
private Seller seller;
private Customer customer;

    /**
     * Constructs a new Sale instance with the specified details.
     * Automatically calculates the total amount based on the provided product list.
     *
     * @param saleId      Unique identifier for the sale.
     * @param products    List of products included in the sale.
     * @param totalAmount Total monetary amount of the transaction.
     * @param date        Date when the sale occurred.
     * @param customer    Customer who made the purchase.
     * @param seller      Seller who processed the transaction.
     */

public Sale(String saleId, List<Product> products, double totalAmount, String date, Customer customer, Seller seller){
    this.saleId=saleId;
    this.totalAmount=calculateTotal();
    //esto es pa que se verifique que la lista de productos no este vacia
    this.products = (products != null) ? products : new ArrayList<>();
    this.date=date;
    this.seller=seller;
    this.customer=customer;

}
    /**
     * Calculates the total sum of prices for all valid products in this sale.
     *
     * @return The total price of all items included in the purchase.
     */
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
    /**
     * Gets the unique sale identifier.
     *
     * @return The sale ID.
     */
    public String getSaleId() {return saleId;}
    public List<Product> getProducts() {return products;}
    public double getTotalAmount() {return totalAmount;}
    public String getDate(){return date;}
    public Seller getSeller(){return seller;}
    public Customer getCustomer(){return customer;}
}
