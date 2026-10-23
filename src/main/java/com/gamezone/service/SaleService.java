package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.PromotionService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Applies sale rules and coordinates inventory, warranty, and sale persistence. */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final List<Sale> sales;
    private WarrantyService warrantyService; // Inyectado vía setter
    private PromotionService promotionService; //tambien inyectado por setters

    /** Creates a sale service without a person service. */
    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this(saleRepository, productService, null, null);
    }

    /** Loads existing sales using the supplied product and person services. */
    public SaleService(SaleRepository saleRepository, ProductService productService,
                       PersonService personService) {
        this(saleRepository, productService, personService, null);
    }

    /**
     * Loads existing sales and accepts the service that manages accessory stock.
     * @param saleRepository repository used to load and save sales
     * @param productService service used to resolve products and update their stock
     * @param personService service used to resolve customers and sellers
     * @param accessoryService service used to update accessory stock
     */
    public SaleService(SaleRepository saleRepository, ProductService productService,
                       PersonService personService, AccessoryService accessoryService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.accessoryService = accessoryService;
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

    /** Sets the service used to evaluate promotions during sale registration. */
    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Registers a sale with promotions, warranties, inventory updates, and persistence.
     * @param sale sale details and requested products
     * @param extendedWarrantyProductIds IDs of consoles receiving an extended warranty
     */
    public void registerSale(Sale sale, List<String> extendedWarrantyProductIds) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null.");
        }
        List<Product> products = sale.getProducts();
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }
        if (sale.getSaleId() == null || sale.getSaleId().isBlank()) {
            throw new IllegalArgumentException("Sale ID cannot be blank.");
        }
        if (getSaleById(sale.getSaleId()) != null) {
            throw new IllegalArgumentException("Sale already exists: " + sale.getSaleId());
        }
        if (sale.getSaleDate() == null) {
            throw new IllegalArgumentException("Sale date is required.");
        }

        Map<String, Integer> quantitiesByProductId = new LinkedHashMap<>();
        Map<String, Product> productsById = new LinkedHashMap<>();
        List<Product> resolvedProducts = new ArrayList<>();
        for (Product requestedProduct : products) {
            Product product = requestedProduct == null ? null
                    : productService.findProductById(requestedProduct.getId());
            if (product == null) {
                throw new IllegalArgumentException("Product is not in inventory: "
                        + (requestedProduct == null ? "null" : requestedProduct.getId()));
            }
            if (product instanceof Accessory && accessoryService != null) {
                product = accessoryService.findById(product.getId());
                if (product == null) {
                    throw new IllegalArgumentException("Accessory is not in inventory: "
                            + requestedProduct.getId());
                }
            }
            resolvedProducts.add(product);
            quantitiesByProductId.merge(product.getId(), 1, Integer::sum);
            productsById.put(product.getId(), product);
        }
        for (Map.Entry<String, Integer> entry : quantitiesByProductId.entrySet()) {
            Product product = productsById.get(entry.getKey());
            if (product.getStockQuantity() < entry.getValue()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getTitle());
            }
        }

        sale.setProducts(resolvedProducts);
        double subtotal = sale.calculateTotal();
        sale.setTotalAmount(subtotal);
        sale.setDiscountAmount(0.0);
        sale.setAppliedPromotionName("");

        if (promotionService != null) {
            Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
            if (bestPromotion != null) {
                double discount = bestPromotion.calculateDiscount(sale);
                discount = Math.max(0.0, Math.min(subtotal, discount));
                sale.setDiscountAmount(discount);
                sale.setAppliedPromotionName(bestPromotion.getName());
            }
        }

        double extendedWarrantyCost = 0.0;
        LocalDate saleDate = sale.getSaleDate();

        for (Product product : resolvedProducts) {
            if (product instanceof Console) {
                boolean wantsExtended = extendedWarrantyProductIds != null
                        && extendedWarrantyProductIds.contains(product.getId());
                if (warrantyService == null) {
                    throw new IllegalStateException("Warranty service is required to sell a console.");
                }
                if (wantsExtended) {
                    ExtendedWarranty warranty = warrantyService.createExtendedWarranty(
                            product, sale, saleDate);
                    extendedWarrantyCost += warranty.getAdditionalCost();
                } else {
                    warrantyService.createBasicWarranty(product, sale, saleDate);
                }
            }
        }

        sale.setTotalAmount(subtotal - sale.getDiscountAmount() + extendedWarrantyCost);

        for (Map.Entry<String, Integer> entry : quantitiesByProductId.entrySet()) {
            Product product = productsById.get(entry.getKey());
            int updatedStock = product.getStockQuantity() - entry.getValue();
            if (product instanceof Accessory && accessoryService != null) {
                accessoryService.updateStock(product.getId(), updatedStock);
            } else {
                productService.updateStock(product.getId(), updatedStock);
            }
        }
        sales.add(sale);
        if (sale.getCustomer() != null) {
            sale.getCustomer().addSaleToHistory(sale);
        }
        saleRepository.saveAll(sales);
        if (warrantyService != null) {
            warrantyService.persistPendingWarranties();
        }
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
