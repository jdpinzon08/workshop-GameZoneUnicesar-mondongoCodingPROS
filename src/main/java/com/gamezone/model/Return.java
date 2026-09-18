package com.gamezone.model;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a return associated with a sale.
 */
public class Return {
    private String id;
    private LocalDate returnDate;
    private Sale sale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    /**
     * Constructs a Return instance and automatically Calculates the total refund amount.
     * @param id id the unique identifier for the return.
     * @param returnDate the date of the return.
     * @param sale the sale associated with the return.
     * @param returnedProducts the list of products returned in the return.
     * @param reason the reason for the return.
     */
    public Return(String id, LocalDate returnDate, Sale sale, List<Product> returnedProducts, String reason){
        this.id = id;
        this.returnDate = returnDate;
        this.sale = sale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }
    /**
     * Calculates the total refund amount for the return.
     * @return the calculated total refund amount
     */
    public double calculateRefundAmount(){
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Product product : returnedProducts) {
            total += product.getPrice();
        }
        return total;
    }
    /**
     * Generates a formatted receipt in Spanish detailing the return information
     * @return a formatted string with return receipt details
     */
    public String generateReturnReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPROBANTE DE DEVOLUCIÓN ===\n");
        sb.append("ID Devolución: ").append(id).append("\n");
        sb.append("Fecha: ").append(returnDate).append("\n");
        sb.append("ID Venta Original: ").append(sale != null ? sale.getSaleId() : "N/A").append("\n");
        sb.append("Motivo: ").append(reason).append("\n");
        sb.append("Productos Devueltos:\n");

        if (returnedProducts != null) {
            for (Product product : returnedProducts) {
                sb.append(" - ").append(product.getTitle())
                        .append(" ($").append(product.getPrice()).append(")\n");
            }
        }

        sb.append("Refunded Amount: $").append(refundAmount).append("\n");
        sb.append("================================");
        return sb.toString();
    }
    //getters
    public String getId() {
        return id;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public Sale getSale() {
        return sale;
    }

    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    public String getReason() {
        return reason;
    }

    public double getRefundAmount() {
        return refundAmount;
    }
}
