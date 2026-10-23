package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service managing business logic for returns and monthly balance reporting.
 */
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Constructs a ReturnService with its required dependencies.
     *
     * @param returnRepository Persistence layer for returns.
     * @param saleService       Service to access sale data.
     * @param productService    Service to handle product updates.
     */
    public ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * Registers a new return with all required business rule validations.
     *
     * @param saleId     Identifier of the original sale.
     * @param productIds List of product IDs to be returned.
     * @param reason     Reason for the return.
     * @return The created Return object.
     * @throws IllegalArgumentException If any validation fails.
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un producto para devolver.");
        }
        if (reason == null || reason.isBlank() || reason.contains("\n") || reason.contains("\r")) {
            throw new IllegalArgumentException("El motivo debe tener texto y ocupar una sola línea.");
        }
        Sale sale = saleService.getSaleById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("La venta indicada no existe.");
        }

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("La devolución supera el plazo máximo permitido de 30 días calendario.");
        }

        Map<String, Integer> purchasedQuantities = new HashMap<>();
        for (Product product : sale.getProducts()) {
            purchasedQuantities.merge(product.getId(), 1, Integer::sum);
        }
        List<Return> existingReturns = returnRepository.loadAll();
        Map<String, Integer> alreadyReturnedQuantities = new HashMap<>();
        for (Return existingReturn : existingReturns) {
            if (existingReturn.getOriginalSale() != null
                    && saleId.equalsIgnoreCase(existingReturn.getOriginalSale().getSaleId())) {
                if (existingReturn.getReturnedProducts() != null) {
                    for (Product product : existingReturn.getReturnedProducts()) {
                        alreadyReturnedQuantities.merge(product.getId(), 1, Integer::sum);
                    }
                }
            }
        }

        List<Product> productsToReturn = new ArrayList<>();
        Map<String, Integer> requestedQuantities = new HashMap<>();

        for (String prodId : productIds) {
            Product product = productService.findProductById(prodId);
            if (product == null || !purchasedQuantities.containsKey(prodId)) {
                throw new IllegalArgumentException("El producto con ID " + prodId + " no pertenece a la venta original.");
            }
            int requested = requestedQuantities.merge(prodId, 1, Integer::sum);
            int previouslyReturned = alreadyReturnedQuantities.getOrDefault(prodId, 0);
            if (previouslyReturned + requested > purchasedQuantities.get(prodId)) {
                throw new IllegalArgumentException("La cantidad del producto " + prodId
                        + " excede las unidades disponibles para devolución.");
            }
            productsToReturn.add(product);
        }

        String returnId = "RET-" + UUID.randomUUID().toString().substring(0, 8);
        Return returnObj = new Return(returnId, LocalDate.now(), sale, productsToReturn, reason, 0.0);
        returnObj.calculateRefundAmount();

        existingReturns.add(returnObj);
        returnRepository.saveAll(existingReturns);
        for (Product product : productsToReturn) {
            productService.restoreStock(product.getId(), 1);
        }

        return returnObj;
    }

    /**
     * Retrieves all registered returns.
     *
     * @return List of all returns.
     */
    public List<Return> viewAllReturns() {
        return returnRepository.loadAll();
    }

    /**
     * Filters returns associated with a specific customer.
     *
     * @param customerId Identifier of the customer.
     * @return List of matching returns.
     */
    public List<Return> viewReturnsByCustomer(String customerId) {
        return returnRepository.loadAll().stream()
                .filter(r -> r.getOriginalSale().getCustomer() != null &&
                        r.getOriginalSale().getCustomer().getId().equalsIgnoreCase(customerId))
                .collect(Collectors.toList());
    }

    /**
     * Filters returns associated with a specific sale.
     *
     * @param saleId Identifier of the sale.
     * @return List of matching returns.
     */
    public List<Return> viewReturnsBySale(String saleId) {
        return returnRepository.loadAll().stream()
                .filter(r -> r.getOriginalSale().getSaleId().equalsIgnoreCase(saleId))
                .collect(Collectors.toList());
    }

    /**
     * Generates net balance report for a given month and year.
     *
     * @param month Month integer (1-12).
     * @param year  Year integer.
     * @return Net total amount (Sales - Returns).
     */
    public double generateMonthlyBalance(int month, int year) {
        double totalSales = saleService.getAllSales().stream()
                .filter(s -> s.getSaleDate() != null &&
                        s.getSaleDate().getMonthValue() == month &&
                        s.getSaleDate().getYear() == year)
                .mapToDouble(Sale::getTotalAmount)
                .sum();

        double totalReturns = returnRepository.loadAll().stream()
                .filter(r -> r.getReturnDate() != null &&
                        r.getReturnDate().getMonthValue() == month &&
                        r.getReturnDate().getYear() == year)
                .mapToDouble(Return::getRefundAmount)
                .sum();

        return totalSales - totalReturns;
    }
}
