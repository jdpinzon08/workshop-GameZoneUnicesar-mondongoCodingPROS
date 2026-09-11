package com.gamezone.model;
import java.util.List;
import java.util.ArrayList;
/**
 * Represents a commercial transaction in the GameZone Unicesar system
 * Connects a Customer, a Seller, and a list of purchased Products
 */

public class Sale {

private String saleId;
private List<Product> products;
private double totalAmount;
private String date;
private Seller seller;
private Customer customer;
/**
    * Constructs a new Sale instance and calculates its total amount
    *
    * @param saleId   Unique identifier for the sale
    * @param date     Transaction date string
    * @param customer Customer making the purchase
    * @param seller   Seller processing the sale
    * @param products List of products included in the sale
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
    * Calculates the total amount of the sale by summing product prices.
    *
    * @return The calculated sum of all non-null products in the sale.
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
     * @return The sale ID string.
     */

    public String getSaleId() {return saleId;}
    /**
     * Gets the list of products included in the sale.
     *
     * @return List of Product instances.
     */
    public List<Product> getProducts() {return products;}

    /**
     * Gets the total monetary value of the sale.
     *
     * @return The total amount.
     */
    public double getTotalAmount() {return totalAmount;}

    /**
     * Gets the transaction date.
     *
     * @return The date string.
     */
    public String getDate(){return date;}

    /**
     * Gets the seller who handled the sale.
     *
     * @return The Seller instance.
     */
    public Seller getSeller(){return seller;}


    /**
     * Gets the customer associated with the sale.
     *
     * @return The Customer instance.
     */
    public Customer getCustomer(){return customer;}

}
