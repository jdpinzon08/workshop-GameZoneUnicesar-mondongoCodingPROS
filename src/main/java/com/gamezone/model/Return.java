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
     * Constructs a Return instance and automatically calculates the total refund amount.
     */
    public Return(String id, LocalDate returnDate, Sale sale, List<Product> returnedProducts, String reason) {
        this.id = id;
        this.returnDate = returnDate;
        this.sale = sale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    /**
     * Overloaded constructor including refundAmount for loading from persistence.
     */
    public Return(String id, LocalDate returnDate, Sale sale, List<Product> returnedProducts, String reason, double refundAmount) {
        this.id = id;
        this.returnDate = returnDate;
        this.sale = sale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = refundAmount;
    }

    /**
     * Calculates the total refund amount for the return.
     */
    public double calculateRefundAmount() {
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Product product : returnedProducts) {
            if (product != null) {
                total += product.getPrice();
            }
        }
        this.refundAmount = total;
        return total;
    }

    /**
     * Generates a formatted receipt in Spanish detailing the return information.
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

        sb.append("Monto Reembolsado: $").append(refundAmount).append("\n");
        sb.append("================================");
        return sb.toString();
    }

    // Getters
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

    /**
     * Returns the original sale associated with the return.
     */
    public Sale getOriginalSale() {
        return this.sale;
    }
}