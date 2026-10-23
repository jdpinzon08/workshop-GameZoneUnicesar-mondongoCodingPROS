package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Persists warranty records as identifiers and scalar values, without service dependencies. */
public class WarrantyRepository {

    private final String filePath;
    private final List<WarrantyDataRecord> records;

    /** Creates a repository backed by the supplied CSV path and loads its records. */
    public WarrantyRepository(String filePath) {
        this.filePath = filePath;
        this.records = new ArrayList<>();
        loadFromFile();
    }

    /** Holds the persisted warranty fields without resolving domain relationships. */
    public static class WarrantyDataRecord {
        private final String warrantyId;
        private final String saleId;
        private final String productId;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String warrantyType;
        private final double additionalCost;

        /** Creates a warranty data record. */
        public WarrantyDataRecord(String warrantyId, String saleId, String productId,
                                  LocalDate startDate, LocalDate endDate,
                                  String warrantyType, double additionalCost) {
            this.warrantyId = warrantyId;
            this.saleId = saleId;
            this.productId = productId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.warrantyType = warrantyType;
            this.additionalCost = additionalCost;
        }

        /** @return unique warranty identifier */
        public String getWarrantyId() { return warrantyId; }
        /** @return associated sale identifier */
        public String getSaleId() { return saleId; }
        /** @return associated product identifier */
        public String getProductId() { return productId; }
        /** @return warranty start date */
        public LocalDate getStartDate() { return startDate; }
        /** @return persisted warranty end date, or {@code null} for legacy records */
        public LocalDate getEndDate() { return endDate; }
        /** @return persisted warranty type */
        public String getWarrantyType() { return warrantyType; }
        /** @return persisted additional cost */
        public double getAdditionalCost() { return additionalCost; }
    }

    /** @return a defensive copy of the loaded raw records */
    public List<WarrantyDataRecord> getRecords() {
        return new ArrayList<>(records);
    }

    /** Persists a warranty record without loading Sale or Product objects. */
    public void save(String warrantyId, String saleId, String productId,
                     LocalDate startDate, LocalDate endDate,
                     String warrantyType, double additionalCost) {
        WarrantyDataRecord record = new WarrantyDataRecord(warrantyId, saleId, productId,
                startDate, endDate, warrantyType, additionalCost);
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Unable to create warranty directory: " + parent);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(String.join(",", warrantyId, saleId, productId, startDate.toString(),
                    endDate.toString(), warrantyType, Double.toString(additionalCost)));
            writer.newLine();
            records.add(record);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing warranty file: " + filePath, e);
        }
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length >= 7) {
                    records.add(new WarrantyDataRecord(fields[0].trim(), fields[1].trim(),
                            fields[2].trim(), LocalDate.parse(fields[3].trim()),
                            LocalDate.parse(fields[4].trim()), fields[5].trim(),
                            Double.parseDouble(fields[6].trim())));
                } else if (fields.length == 5) {
                    // Legacy format: type,id,productId,saleId,startDate.
                    String type = fields[0].trim();
                    LocalDate start = LocalDate.parse(fields[4].trim());
                    int duration = "EXTENDED".equalsIgnoreCase(type) ? 12 : 6;
                    records.add(new WarrantyDataRecord(fields[1].trim(), fields[3].trim(),
                            fields[2].trim(), start, start.plusMonths(duration), type, 0.0));
                }
            }
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Error reading warranty file: " + filePath, e);
        }
    }
}
