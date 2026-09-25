package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Applies sale rules and coordinates inventory, warranty, and sale persistence. */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final List<Sale> sales;
    private WarrantyService warrantyService; // Inyectado vía setter

    /** Creates a sale service without a person service. */
    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this(saleRepository, productService, null);
    }

    /** Loads existing sales using the supplied product and person services. */
    public SaleService(SaleRepository saleRepository, ProductService productService,
                       PersonService personService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        List<Customer> customers = personService == null
                ? new ArrayList<>() : personService.getAllCustomers();
        List<Seller> sellers = personService == null
                ? new ArrayList<>() : personService.getAllSellers();
        this.sales = new ArrayList<>(saleRepository.findAll(
                productService.getAllProducts(), customers, sellers));
        for (Sale sale : sales) {
            if (sale.getCustomer() != null) {
                sale.getCustomer().addSaleToHistory(sale);
            }
        }
    }

    /** Sets the service used to assign warranties during sale registration. */
    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    /** Validates and records a sale, applying selected extended warranties. */
    public void registerSale(Sale sale, List<String> extendedWarrantyProductIds) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null.");
        }
        if (sale.getSaleId() == null || sale.getSaleId().isBlank()) {
            throw new IllegalArgumentException("Sale ID cannot be blank.");
        }
        if (getSaleById(sale.getSaleId()) != null) {
            throw new IllegalArgumentException("Sale already exists: " + sale.getSaleId());
        }
        List<Product> products = sale.getProducts();
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }

        Map<String, Integer> quantitiesByProductId = new LinkedHashMap<>();
        Map<String, Product> productsById = new LinkedHashMap<>();
        for (Product requestedProduct : products) {
            Product product = requestedProduct == null ? null
                    : productService.findProductById(requestedProduct.getId());
            if (product == null) {
                throw new IllegalArgumentException("Product is not in inventory: "
                        + (requestedProduct == null ? "null" : requestedProduct.getId()));
            }
            quantitiesByProductId.merge(product.getId(), 1, Integer::sum);
            productsById.put(product.getId(), product);
        }
        for (Map.Entry<String, Integer> entry : quantitiesByProductId.entrySet()) {
            Product product = productsById.get(entry.getKey());
            if (product.getStockQuantity() < entry.getValue()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getTitle());
            }
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

        for (Map.Entry<String, Integer> entry : quantitiesByProductId.entrySet()) {
            Product product = productsById.get(entry.getKey());
            productService.updateStock(product.getId(), product.getStockQuantity() - entry.getValue());
        }
        sale.setTotalAmount(sale.calculateTotal() + extraCost);
        sales.add(sale);
        if (sale.getCustomer() != null) {
            sale.getCustomer().addSaleToHistory(sale);
        }
        saleRepository.saveAll(sales);
    }

    /** Returns a copy of the registered sales. */
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

    /** Returns sales associated with the given customer ID. */
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

    /** Returns sales associated with the given seller ID. */
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
