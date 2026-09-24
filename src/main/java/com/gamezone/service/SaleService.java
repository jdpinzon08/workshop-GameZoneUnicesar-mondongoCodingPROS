package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final List<Sale> sales;
    private WarrantyService warrantyService; // Inyectado vía setter

    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.sales = new ArrayList<>();
    }

    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    public void registerSale(Sale sale, List<String> extendedWarrantyProductIds) {
        if (sale == null) throw new IllegalArgumentException("Sale cannot be null.");
        List<Product> products = sale.getProducts();
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }

        for (Product product : products) {
            if (product.getStockQuantity() <= 0) {
                throw new IllegalArgumentException("Product out of stock: " + product.getTitle());
            }
        }

        for (Product product : products) {
            productService.updateStock(product.getId(), product.getStockQuantity() - 1);
        }

        double extraCost = 0.0;
        LocalDate saleDate = LocalDate.now();

        // Corrección del bucle de garantías
        if (warrantyService != null) {
            for (Product product : products) {
                if (product instanceof Console) {
                    boolean wantsExtended = extendedWarrantyProductIds != null
                            && extendedWarrantyProductIds.contains(product.getId());

                    if (wantsExtended) {
                        ExtendedWarranty ext =
                                warrantyService.assignExtendedWarranty(product, sale, saleDate);
                        extraCost += ext.getAdditionalCost();
                    } else {
                        warrantyService.assignBasicWarranty(product, sale, saleDate);
                    }
                }
            }
        }

        sales.add(sale);
        saleRepository.saveAll(sales);
    }

    public List<Sale> getAllSales() {
        return new ArrayList<>(sales);
    }

    /** Returns a registered sale by its saleId, or {@code null} when absent. */
    public Sale getSaleById(String saleId) {
        if (saleId == null || saleId.trim().isEmpty()) {
            return null;
        }
        for (Sale sale : sales) {
            if (sale != null && saleId.equalsIgnoreCase(sale.getSaleId())) {
                return sale;
            }
        }
        return null;
    }

    public List<Sale> getSalesByCustomer(String customerId) {
        List<Sale> result = new ArrayList<>();

        if (customerId != null && !customerId.trim().isEmpty()) {
            for (Sale sale : sales) {
                if (sale.getCustomer() != null
                        && customerId.equalsIgnoreCase(sale.getCustomer().getId())) {
                    result.add(sale);
                }
            }
        }

        return result;
    }

    public List<Sale> getSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();

        if (sellerId != null && !sellerId.trim().isEmpty()) {
            for (Sale sale : sales) {
                if (sale.getSeller() != null
                        && sellerId.equalsIgnoreCase(sale.getSeller().getId())) {
                    result.add(sale);
                }
            }
        }

        return result;
    }
}
