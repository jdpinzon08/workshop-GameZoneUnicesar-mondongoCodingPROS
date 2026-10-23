package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.Console;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Coordinates warranty rules and resolves persisted identifiers into domain objects. */
public class WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final List<Warranty> warranties;
    private final List<Warranty> pendingWarranties;

    /**
     * Creates the warranty service and resolves records after sales and products are loaded.
     *
     * @param warrantyRepository repository containing raw warranty records
     * @param saleRepository repository used to resolve sale identifiers
     * @param productService service used to resolve product identifiers
     */
    public WarrantyService(WarrantyRepository warrantyRepository, SaleRepository saleRepository,
                           ProductService productService) {
        this.warrantyRepository = warrantyRepository;
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.warranties = new ArrayList<>();
        this.pendingWarranties = new ArrayList<>();
        loadWarranties();
    }

    private void loadWarranties() {
        for (WarrantyRepository.WarrantyDataRecord record : warrantyRepository.getRecords()) {
            Sale sale = saleRepository.findById(record.getSaleId());
            Product product = productService.findProductById(record.getProductId());
            if (sale == null || product == null) {
                continue;
            }
            if ("EXTENDED".equalsIgnoreCase(record.getWarrantyType())) {
                warranties.add(new ExtendedWarranty(record.getWarrantyId(), product, sale,
                        record.getStartDate()));
            } else if ("BASIC".equalsIgnoreCase(record.getWarrantyType())) {
                warranties.add(new BasicWarranty(record.getWarrantyId(), product, sale,
                        record.getStartDate()));
            }
        }
    }

    /** Assigns the automatic six-month warranty to a console. */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        BasicWarranty warranty = createBasicWarranty(product, sale, startDate);
        persistPendingWarranties();
        return warranty;
    }

    /**
     * Creates the automatic six-month warranty without persisting it yet.
     * @param product console covered by the warranty
     * @param sale sale associated with the warranty
     * @param startDate warranty start date
     * @return the staged basic warranty
     */
    public BasicWarranty createBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        validateWarrantyData(product, sale, startDate);
        if (!(product instanceof Console)) {
            throw new IllegalArgumentException("Basic warranties are available only for consoles.");
        }
        BasicWarranty warranty = new BasicWarranty(generateWarrantyId(), product, sale, startDate);
        warranties.add(warranty);
        pendingWarranties.add(warranty);
        return warranty;
    }

    /** Assigns a twelve-month extended warranty to a console. */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        ExtendedWarranty warranty = createExtendedWarranty(product, sale, startDate);
        persistPendingWarranties();
        return warranty;
    }

    /**
     * Creates a twelve-month extended warranty without persisting it yet.
     * @param product console covered by the warranty
     * @param sale sale associated with the warranty
     * @param startDate warranty start date
     * @return the staged extended warranty
     */
    public ExtendedWarranty createExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        validateWarrantyData(product, sale, startDate);
        if (!(product instanceof Console)) {
            throw new IllegalArgumentException("Extended warranties are available only for consoles.");
        }
        ExtendedWarranty warranty = new ExtendedWarranty(generateWarrantyId(), product, sale, startDate);
        warranties.add(warranty);
        pendingWarranties.add(warranty);
        return warranty;
    }

    /** Persists warranties staged by the create methods. */
    public void persistPendingWarranties() {
        while (!pendingWarranties.isEmpty()) {
            Warranty warranty = pendingWarranties.get(0);
            persist(warranty);
            pendingWarranties.remove(0);
        }
    }

    private void persist(Warranty warranty) {
        warrantyRepository.save(warranty.getId(), warranty.getSale().getSaleId(),
                warranty.getProduct().getId(), warranty.getStartDate(), warranty.getEndDate(),
                warranty instanceof ExtendedWarranty ? "EXTENDED" : "BASIC",
                warranty.getAdditionalCost());
    }

    private void validateWarrantyData(Product product, Sale sale, LocalDate startDate) {
        if (product == null || sale == null || startDate == null) {
            throw new IllegalArgumentException("Product, sale, and start date are required.");
        }
    }

    private String generateWarrantyId() {
        int next = warranties.size() + 1;
        String id;
        do {
            id = "WARRANTY-" + next++;
        } while (idExists(id));
        return id;
    }

    private boolean idExists(String id) {
        return warranties.stream().anyMatch(warranty -> warranty.getId().equalsIgnoreCase(id));
    }

    /** Finds a warranty using its product and sale identifiers. */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        return warranties.stream().filter(warranty -> warranty.getProduct().getId().equalsIgnoreCase(productId)
                && warranty.getSale().getSaleId().equalsIgnoreCase(saleId)).findFirst().orElse(null);
    }

    /** @return a copy of all registered warranties */
    public List<Warranty> listAllWarranties() {
        return new ArrayList<>(warranties);
    }

    /** @return warranties active today */
    public List<Warranty> listActiveWarranties() {
        LocalDate today = LocalDate.now();
        return warranties.stream().filter(warranty -> warranty.isActive(today)).toList();
    }

    /** Returns warranties expiring from today through the requested number of days. */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        if (daysAhead < 0) {
            throw new IllegalArgumentException("Days ahead cannot be negative.");
        }
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);
        return warranties.stream().filter(warranty -> !warranty.getEndDate().isBefore(today)
                && !warranty.getEndDate().isAfter(limit)).toList();
    }

    /** @return a copy of every registered warranty */
    public List<Warranty> getAllWarranties() {
        return listAllWarranties();
    }
}
