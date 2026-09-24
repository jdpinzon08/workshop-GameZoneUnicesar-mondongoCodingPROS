package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists warranty records in CSV format.
 */
public class WarrantyRepository {

    private final String filePath;
    private final ProductService productService;
    private final SaleService saleService;

    public WarrantyRepository(ProductService productService, SaleService saleService) {
        this("data/warranties.csv", productService, saleService);
    }

    public WarrantyRepository(String filePath, ProductService productService,
                              SaleService saleService) {
        this.filePath = filePath;
        this.productService = productService;
        this.saleService = saleService;
    }

    /**
     * Saves all warranties in the CSV file.
     */
    public void saveAll(List<Warranty> warranties) {
        try {
            Path path = Path.of(filePath);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            List<String> lines = new ArrayList<>();

            for (Warranty warranty : warranties) {
                String type;

                if (warranty instanceof BasicWarranty) {
                    type = "BASIC";
                } else if (warranty instanceof ExtendedWarranty) {
                    type = "EXTENDED";
                } else {
                    continue;
                }

                lines.add(
                        type + "," +
                                warranty.getId() + "," +
                                warranty.getProduct().getId() + "," +
                                warranty.getSale().getSaleId() + "," +
                                warranty.getStartDate()
                );
            }

            Files.write(path, lines);

        } catch (IOException e) {
            throw new RuntimeException("Error saving warranties.", e);
        }
    }

    /**
     * Loads warranties from the CSV file.
     *
     * @return all warranties, or an empty list when the file does not exist
     */
    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            return warranties;
        }

        try {
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                if (parts.length != 5) {
                    continue;
                }

                String type = parts[0];
                String warrantyId = parts[1];
                String productId = parts[2];
                String saleId = parts[3];
                LocalDate startDate = LocalDate.parse(parts[4]);

                Product product = productService.findProductById(productId);
                Sale sale = saleService.getSaleById(saleId);

                if (product == null || sale == null) {
                    continue;
                }

                if ("BASIC".equalsIgnoreCase(type)) {
                    warranties.add(
                            new BasicWarranty(warrantyId, product, sale, startDate)
                    );
                } else if ("EXTENDED".equalsIgnoreCase(type)) {
                    warranties.add(
                            new ExtendedWarranty(warrantyId, product, sale, startDate)
                    );
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error loading warranties.", e);
        }

        return warranties;
    }
}
