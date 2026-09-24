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
import java.util.Locale;

/**
 * Handles persistence operations for Return entities using a CSV file.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";
    private final SaleService saleService;
    private final ProductService productService;

    public ReturnRepository(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    public void saveAll(List<Return> returns) {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Return ret : returns) {
                StringBuilder productIds = new StringBuilder();
                List<Product> returnedProds = ret.getReturnedProducts();
                if (returnedProds != null) {
                    for (int i = 0; i < returnedProds.size(); i++) {
                        productIds.append(returnedProds.get(i).getId());
                        if (i < returnedProds.size() - 1) {
                            productIds.append(";");
                        }
                    }
                }

                String[] fields = {
                        ret.getId(),
                        ret.getReturnDate() == null ? "" : ret.getReturnDate().toString(),
                        ret.getOriginalSale() != null ? ret.getOriginalSale().getSaleId() : "",
                        productIds.toString(),
                        ret.getReason(),
                        String.format(Locale.US, "%.2f", ret.getRefundAmount())
                };
                writer.write(toCsvRecord(fields));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error saving returns.", e);
        }
    }

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
                String[] parts = parseCsvRecord(line);
                if (parts.length >= 6) {
                    String id = parts[0];
                    LocalDate returnDate = LocalDate.parse(parts[1]);
                    String saleId = parts[2];
                    String[] productIds = parts[3].isEmpty() ? new String[0] : parts[3].split(";");
                    String reason = parts[4];
                    double refundAmount = Double.parseDouble(parts[5]);

                    Sale sale = saleService.getSaleById(saleId);
                    List<Product> products = new ArrayList<>();
                    for (String prodId : productIds) {
                        Product p = productService.findProductById(prodId);
                        if (p != null) {
                            products.add(p);
                        }
                    }

                    if (sale != null) {
                        Return ret = new Return(id, returnDate, sale, products, reason, refundAmount);
                        returns.add(ret);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error loading returns.", e);
        }

        return returns;
    }

    private String toCsvRecord(String[] fields) {
        List<String> escapedFields = new ArrayList<>();
        for (String field : fields) {
            String value = field == null ? "" : field;
            if (value.contains(",") || value.contains("\"")
                    || value.contains("\n") || value.contains("\r")) {
                value = "\"" + value.replace("\"", "\"\"") + "\"";
            }
            escapedFields.add(value);
        }
        return String.join(",", escapedFields);
    }

    private String[] parseCsvRecord(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (quoted && current == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                field.append('"');
                i++;
            } else if (current == '"') {
                quoted = !quoted;
            } else if (current == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }
}
