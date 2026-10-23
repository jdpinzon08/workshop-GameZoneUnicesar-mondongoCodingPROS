package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;

/**
 * Static validation utility for product operations.
 */
public final class ProductValidator {

    private ProductValidator() {
        // Prevent instantiation
    }

    /**
     * Validates product fields on registration or update.
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("id", "no puede estar vacío");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("title", "no puede estar vacío");
        }
        if (price <= 0) {
            throw new InvalidDataException("price", "debe ser mayor a cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "no puede ser negativo");
        }
    }

    /**
     * Validates that the product has enough stock to fulfill the requested quantity.
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        validateProductExists(product, "desconocido");
        if (requestedQuantity <= 0) {
            throw new InvalidDataException("quantity", "la cantidad solicitada debe ser mayor a cero");
        }
        if (product.getStockQuantity() < requestedQuantity) {
            throw new BusinessRuleException("Stock insuficiente para el producto '" + product.getTitle()
                    + "'. Disponible: " + product.getStockQuantity() + ", Solicitado: " + requestedQuantity);
        }
    }

    /**
     * Validates that the product instance is not null.
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }
}