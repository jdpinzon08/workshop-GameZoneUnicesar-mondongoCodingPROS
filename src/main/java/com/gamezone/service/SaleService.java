package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.persistence.SaleRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer component responsible for processing sales transactions,
 * enforcing business rules, checking product stock, and coordinating persistence.
 *
 * @author Technical Lead (mondongoCodingPROS)
 * @version 1.0.0
 */
public class SaleService {

    /** Repository for persisting and retrieving sales records. */
    private final SaleRepository saleRepository;

    /** Service dependency used to manage and update product stock levels. */
    private final ProductService productService;

    /** In-memory list of sales to optimize reads during session execution. */
    private final List<Sale> sales;

    /**
     * Constructs a SaleService with required repository and service dependencies.
     *
     * @param saleRepository Persistence repository for sales.
     * @param productService Service component for managing product inventory.
     */
    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.sales = new ArrayList<>();
    }

    /**
     * Registers a new sale transaction in the system after validating constraints.
     * Verifies non-empty product list, ensures stock availability, decrements stock,
     * and persists the transaction.
     *
     * @param sale The Sale transaction to register.
     * @throws IllegalArgumentException If the product list is empty or stock is insufficient.
     */
    public void registerSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null.");
        }

        List<Product> products = sale.getProducts();
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }

        // 1. Verify stock availability for all products in the sale
        for (Product product : products) {
            if (product.getStockQuantity() <= 0) {
                throw new IllegalArgumentException("Product out of stock: " + product.getTitle());
            }
        }

        // 2. Decrement inventory stock for each sold product
        for (Product product : products) {
            int currentStock = product.getStockQuantity();
            productService.updateStock(product.getId(), currentStock - 1);
        }

        // 3. Save to memory and persist to CSV file
        sales.add(sale);
        saleRepository.saveAll(sales);
    }

    /**
     * Retrieves all sales registered in the current session.
     *
     * @return List of all Sale instances.
     */
    public List<Sale> getAllSales() {
        return new ArrayList<>(sales);
    }

    /**
     * Filters sales processed for a specific customer ID.
     *
     * @param customerId The unique identifier of the customer.
     * @return List of Sale instances matching the given customer ID.
     */
    public List<Sale> getSalesByCustomer(String customerId) {
        List<Sale> result = new ArrayList<>();
        if (customerId != null && !customerId.trim().isEmpty()) {
            for (Sale sale : sales) {
                if (sale.getCustomer() != null && customerId.equalsIgnoreCase(sale.getCustomer().getId())) {
                    result.add(sale);
                }
            }
        }
        return result;
    }

    /**
     * Filters sales processed by a specific seller employee code or ID.
     *
     * @param sellerId The unique identifier or code of the seller.
     * @return List of Sale instances matching the given seller code.
     */
    public List<Sale> getSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();
        if (sellerId != null && !sellerId.trim().isEmpty()) {
            for (Sale sale : sales) {
                if (sale.getSeller() != null && sellerId.equalsIgnoreCase(sale.getSeller().getId())) {
                    result.add(sale);
                }
            }
        }
        return result;
    }
}