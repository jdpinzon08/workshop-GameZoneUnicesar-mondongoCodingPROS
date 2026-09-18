package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides business logic for warranty management.
 */
public class WarrantyService {

    private final WarrantyRepository repository;
    private final List<Warranty> warranties;

    public WarrantyService(WarrantyRepository repository) {
        this.repository = repository;
        this.warranties = repository.loadAll();
    }

    /**
     * Creates and persists a basic warranty.
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale,
                                             LocalDate startDate) {
        validateWarrantyData(product, sale, startDate);

        BasicWarranty warranty = new BasicWarranty(
                generateWarrantyId(),
                product,
                sale,
                startDate
        );

        warranties.add(warranty);
        repository.saveAll(warranties);

        return warranty;
    }

    /**
     * Creates and persists an extended warranty.
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale,
                                                   LocalDate startDate) {
        validateWarrantyData(product, sale, startDate);

        ExtendedWarranty warranty = new ExtendedWarranty(
                generateWarrantyId(),
                product,
                sale,
                startDate
        );

        warranties.add(warranty);
        repository.saveAll(warranties);

        return warranty;
    }

    /**
     * Finds the warranty for a product within a specific sale.
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty warranty : warranties) {
            boolean sameProduct = warranty.getProduct().getId().equals(productId);
            boolean sameSale = warranty.getSale().getSaleId().equals(saleId);

            if (sameProduct && sameSale) {
                return warranty;
            }
        }

        return null;
    }

    /**
     * Returns every registered warranty.
     */
    public List<Warranty> listAllWarranties() {
        return new ArrayList<>(warranties);
    }

    /**
     * Returns warranties active on the current date.
     */
    public List<Warranty> listActiveWarranties() {
        List<Warranty> activeWarranties = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Warranty warranty : warranties) {
            if (warranty.isActive(today)) {
                activeWarranties.add(warranty);
            }
        }

        return activeWarranties;
    }

    /**
     * Returns warranties expiring from today through the requested number of days.
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        if (daysAhead < 0) {
            throw new IllegalArgumentException(
                    "Los días de anticipación no pueden ser negativos."
            );
        }

        List<Warranty> expiringWarranties = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(daysAhead);

        for (Warranty warranty : warranties) {
            LocalDate endDate = warranty.getEndDate();

            if (!endDate.isBefore(today) && !endDate.isAfter(limitDate)) {
                expiringWarranties.add(warranty);
            }
        }

        return expiringWarranties;
    }

    private void validateWarrantyData(Product product, Sale sale,
                                      LocalDate startDate) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (sale == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }

        if (startDate == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula.");
        }
    }

    private String generateWarrantyId() {
        int nextNumber = warranties.size() + 1;
        String warrantyId = "WARRANTY-" + nextNumber;

        while (idExists(warrantyId)) {
            nextNumber++;
            warrantyId = "WARRANTY-" + nextNumber;
        }

        return warrantyId;
    }

    private boolean idExists(String warrantyId) {
        for (Warranty warranty : warranties) {
            if (warranty.getId().equals(warrantyId)) {
                return true;
            }
        }

        return false;
    }
}