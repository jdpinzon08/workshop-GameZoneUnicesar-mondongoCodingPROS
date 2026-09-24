package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Persists complete sale records and resolves their domain relationships. */
public class SaleRepository {

    private final String filePath;

    /** Uses the specified file to store sale records. */
    public SaleRepository(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /** Uses the default sales CSV file. */
    public SaleRepository() {
        this("data/sales.csv");
    }

    private void ensureFileExists() {
        File file = new File(filePath);
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error creating sales file: " + filePath, e);
        }
    }

    /** CSV fields: saleId,date,customerId,sellerId,productIds,totalAmount. */
    public void saveAll(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Sale sale : sales) {
                String productIds = sale.getProducts().stream()
                        .map(Product::getId)
                        .reduce((left, right) -> left + "|" + right)
                        .orElse("");
                writer.write(String.join(",",
                        sale.getSaleId(),
                        sale.getDate(),
                        sale.getCustomer() == null ? "" : sale.getCustomer().getId(),
                        sale.getSeller() == null ? "" : sale.getSeller().getId(),
                        productIds,
                        Double.toString(sale.getTotalAmount())));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error writing sales file: " + filePath, e);
        }
    }

    /** Loads sales and reconnects IDs to the shared Product, Customer, and Seller objects. */
    public List<Sale> findAll(List<Product> products, List<Customer> customers,
                              List<Seller> sellers) {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length < 5) {
                    continue;
                }

                List<Product> saleProducts = new ArrayList<>();
                boolean allProductsFound = true;
                if (!fields[4].isEmpty()) {
                    for (String productId : fields[4].split("\\|", -1)) {
                        Product product = findProduct(products, productId);
                        if (product != null) {
                            saleProducts.add(product);
                        } else {
                            allProductsFound = false;
                        }
                    }
                }
                if (!allProductsFound || saleProducts.isEmpty()) {
                    continue;
                }
                Customer customer = findCustomer(customers, fields[2]);
                Seller seller = findSeller(sellers, fields[3]);
                if (customer == null && !fields[2].isBlank()) {
                    customer = new Customer(fields[2], "Customer " + fields[2], "N/A", "N/A");
                }
                if (seller == null && !fields[3].isBlank()) {
                    seller = new Seller(fields[3], "Seller " + fields[3], "N/A",
                            fields[3], "N/A");
                }
                double total = fields.length > 5 && !fields[5].isBlank()
                        ? Double.parseDouble(fields[5])
                        : saleProducts.stream().mapToDouble(Product::getPrice).sum();
                sales.add(new Sale(fields[0], saleProducts, total, fields[1], customer, seller));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading sales file: " + filePath, e);
        }
        return sales;
    }

    /** Retained for callers that need to inspect the stored CSV records. */
    public List<String> findAllLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading sales file: " + filePath, e);
        }
        return lines;
    }

    private Product findProduct(List<Product> products, String productId) {
        return products.stream().filter(p -> p.getId().equals(productId)).findFirst().orElse(null);
    }

    private Customer findCustomer(List<Customer> customers, String customerId) {
        return customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
    }

    private Seller findSeller(List<Seller> sellers, String sellerId) {
        return sellers.stream().filter(s -> s.getId().equals(sellerId)).findFirst().orElse(null);
    }
}
