package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
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

/**
 * Persists complete sale records and resolves their domain relationships.
 */
public class SaleRepository {

    private final String filePath;
    private List<Sale> loadedSales = new ArrayList<>();

    /**
     * Uses the specified file to store sale records.
     *
     * @param filePath path where sale records will be stored
     * @throws PersistenceException if the sales file cannot be created or accessed
     */
    public SaleRepository(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * Uses the default sales CSV file.
     *
     * @throws PersistenceException if the default sales file cannot be created or accessed
     */
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
            throw new PersistenceException("Error al crear o verificar el archivo de ventas: " + filePath, e);
        }
    }

    /**
     * Saves sales using the CSV fields saleId,date,customerId,sellerId,productIds,totalAmount,
     * appliedPromotionName, and discountAmount.
     *
     * @param sales sales to persist
     * @throws PersistenceException if writing the sales file fails
     */
    public void saveAll(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Sale sale : sales) {
                String productIds = sale.getProducts().stream()
                        .map(Product::getId)
                        .reduce((left, right) -> left + "|" + right)
                        .orElse("");

                String appliedPromo = sale.getAppliedPromotionName() != null ? sale.getAppliedPromotionName() : "";
                String discountStr = Double.toString(sale.getDiscountAmount());

                writer.write(String.join(",",
                        sale.getSaleId(),
                        sale.getDate(),
                        sale.getCustomer() == null ? "" : sale.getCustomer().getId(),
                        sale.getSeller() == null ? "" : sale.getSeller().getId(),
                        productIds,
                        Double.toString(sale.getTotalAmount()),
                        appliedPromo,
                        discountStr));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al escribir las ventas en el archivo: " + filePath, e);
        }
    }

    /**
     * Loads sales and reconnects IDs to the shared Product, Customer, and Seller objects.
     *
     * @param products products available for resolving sale product identifiers
     * @param customers customers available for resolving customer identifiers
     * @param sellers sellers available for resolving seller identifiers
     * @return loaded sales with their related objects resolved
     * @throws PersistenceException if reading the sales file fails
     */
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

                String appliedPromo = (fields.length > 6 && !fields[6].isBlank()) ? fields[6] : "";

                double discountAmount = 0.0;
                if (fields.length > 7 && !fields[7].isBlank()) {
                    try {
                        discountAmount = Double.parseDouble(fields[7]);
                    } catch (NumberFormatException ignored) {
                        discountAmount = 0.0;
                    }
                }

                Sale sale = new Sale(fields[0], saleProducts, total, fields[1], customer, seller, appliedPromo, discountAmount);
                sales.add(sale);
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al leer el historial de ventas desde el archivo: " + filePath, e);
        }
        loadedSales = new ArrayList<>(sales);
        return sales;
    }

    /**
     * Returns a sale previously resolved by {@link #findAll(List, List, List)}.
     *
     * @param saleId identifier of the sale to find
     * @return the matching sale, or {@code null} when no sale matches
     */
    public Sale findById(String saleId) {
        if (saleId == null || saleId.isBlank()) {
            return null;
        }
        return loadedSales.stream()
                .filter(sale -> saleId.equalsIgnoreCase(sale.getSaleId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns nonblank CSV records from the sales file.
     *
     * @return stored nonblank sale records
     * @throws PersistenceException if reading the sales file fails
     */
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
            throw new PersistenceException("Error al leer las líneas del archivo de ventas: " + filePath, e);
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
