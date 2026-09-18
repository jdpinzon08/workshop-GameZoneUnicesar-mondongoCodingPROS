package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence operations for Return entities using a CSV file.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Constructs a ReturnRepository with required service dependencies for data resolution.
     *
     * @param saleService    Service to look up sales.
     * @param productService Service to look up products.
     */
    public ReturnRepository(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * Saves all returns to the CSV file.
     *
     * @param returns The list of returns to persist.
     */
    public void saveAll(List<Return> returns) {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Return ret : returns) {
                StringBuilder productIds = new StringBuilder();
                for (int i = 0; i < ret.getReturnedProducts().size(); i++) {
                    productIds.append(ret.getReturnedProducts().get(i).getId());
                    if (i < ret.getReturnedProducts().size() - 1) {
                        productIds.append(";");
                    }
                }

                String line = String.format("%s,%s,%s,%s,%s,%.2f",
                        ret.getId(),
                        ret.getReturnDate(),
                        ret.getOriginalSale().getId(),
                        productIds.toString(),
                        ret.getReason(),
                        ret.getRefundAmount());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving returns: " + e.getMessage());
        }
    }

    /**
     * Loads all returns from the CSV file.
     *
     * @return List of Return objects, or empty list if the file does not exist.
     */
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return returns;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String id = parts[0];
                    LocalDate returnDate = LocalDate.parse(parts[1]);
                    String saleId = parts[2];
                    String[] productIds = parts[3].split(";");
                    String reason = parts[4];
                    double refundAmount = Double.parseDouble(parts[5]);

                    Sale sale = (Sale) saleService.getSalesBySeller(saleId);
                    List<Product> products = new ArrayList<>();
                    for (String prodId : productIds) {
                        Product p = productService.findProductById(prodId);
                        if (p != null) {
                            products.add(p);
                        }
                    }

                    if (sale != null) {
                        Return ret = new Return(id, returnDate, sale, products, reason);
                        returns.add(ret);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading returns: " + e.getMessage());
        }

        return returns;
    }
}