package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import java.util.List;

/**
 * Static validation utility for sale operations.
 */
public final class SaleValidator {

    private SaleValidator() {
        // Prevent instantiation
    }

    /**
     * Validates entities and items involved in a sale.
     */
    public static void validateSaleData(Customer customer, Seller seller, List<Product> products) {
        if (customer == null) {
            throw new InvalidDataException("customer", "el cliente asociado no puede ser nulo");
        }
        if (seller == null) {
            throw new InvalidDataException("seller", "el vendedor asociado no puede ser nulo");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException("Una venta debe incluir al menos un producto");
        }
    }

    /**
     * Validates that the sale instance is not null.
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}