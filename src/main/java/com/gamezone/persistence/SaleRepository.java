package com.gamezone.persistence;
import com.gamezone.model.Sale;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Persistence layer repository for managing Sale entity file storage.
 * Handles reading and writing sales transaction data in CSV format.
 *
 * @author Technical Lead (mondongoCodingPROS)
 * @version 1.0.0
 */

public class SaleRepository {

/** Path to the CSV storage file, the place where all this madness is going to be xD */
private String filePath;

    /**
     * Constructs a SaleRepository with a custom file path
     *
     * @param filePath Relative or absolute path to the data storage file
     */
public SaleRepository(String filePath){
    this.filePath=filePath;
    ensureFileExists();
}
/**
* Default constructor setting the storage file to 'data/sales.csv'.
*/

public SaleRepository() {
    this("data/sales.csv");
}
//pa que esa vaina funcione ome

/**
    * Ensures that the parent directories and the target CSV file exist on the disk.
    * Creates them automatically if they do not already exist.
*/
private void ensureFileExists() {
    try {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        } catch (IOException e) {
            System.err.println("Error creating data file: " + e.getMessage());
        }
    }

/**
    * Overwrites the CSV storage file with the complete list of sales.
    * Converts each Sale instance into a formatted CSV record line.
    *
    * @param sales List of Sale entities to persist in storage.
*/
    public void saveAll(List<Sale> sales) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (Sale sale : sales) {
                // Se convierte el objeto Sale a formato CSV
                String line = String.format("%s,%s,%s,%s,%s",
                        sale.getSaleId(),
                        sale.getDate(),
                        sale.getCustomer().getId(),
                        sale.getSeller().getId(),
                        joinProductIds(sale)
                );
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    /**
     * Reads all non-empty line records from the sales CSV storage file.
     *
     * @return List of raw CSV formatted strings representing sales records.
     */
    public List<String> findAllLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }
    /**
     * Helper method to extract and concatenate all product IDs from a sale
     * into a single pipe-separated String (e.g., "P001|P002").
     *
     * @param sale The Sale entity containing products.
     * @return A pipe-separated String of product IDs.
     */
    private String joinProductIds(Sale sale) {
        List<String> ids = new ArrayList<>();
        sale.getProducts().forEach(p -> ids.add(p.getId()));
        return String.join("|", ids);
    }
}





