package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repository class responsible for persisting Accessory objects to CSV storage.
 */
public class AccessoryRepository {

    private static final String FILE_PATH = "data/accessories.csv";
    private static final String DELIMITER = ",";
    private static final String LIST_DELIMITER = ";";

    /**
     * Saves all accessories to the CSV file using a type discriminator.
     *
     * @param accessories The list of accessories to persist.
     */
    public void saveAll(List<Accessory> accessories) {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Accessory accessory : accessories) {
                StringBuilder line = new StringBuilder();

                if (accessory instanceof Controller) {
                    Controller c = (Controller) accessory;
                    line.append("CONTROLLER").append(DELIMITER)
                            .append(c.getId()).append(DELIMITER)
                            .append(c.getTitle()).append(DELIMITER)
                            .append(c.getPrice()).append(DELIMITER)
                            .append(c.getStockQuantity()).append(DELIMITER)
                            .append(String.join(LIST_DELIMITER, c.getCompatibleConsoles())).append(DELIMITER)
                            .append(c.getConnectionType());
                } else if (accessory instanceof Cable) {
                    Cable c = (Cable) accessory;
                    line.append("CABLE").append(DELIMITER)
                            .append(c.getId()).append(DELIMITER)
                            .append(c.getTitle()).append(DELIMITER)
                            .append(c.getPrice()).append(DELIMITER)
                            .append(c.getStockQuantity()).append(DELIMITER)
                            .append(String.join(LIST_DELIMITER, c.getCompatibleConsoles())).append(DELIMITER)
                            .append(c.getLengthInMeters()).append(DELIMITER)
                            .append(c.getConnectorType());
                } else if (accessory instanceof Memory) {
                    Memory m = (Memory) accessory;
                    line.append("MEMORY").append(DELIMITER)
                            .append(m.getId()).append(DELIMITER)
                            .append(m.getTitle()).append(DELIMITER)
                            .append(m.getPrice()).append(DELIMITER)
                            .append(m.getStockQuantity()).append(DELIMITER)
                            .append(String.join(LIST_DELIMITER, m.getCompatibleConsoles())).append(DELIMITER)
                            .append(m.getCapacityInGB()).append(DELIMITER)
                            .append(m.getMemoryType());
                }
                writer.println(line.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving accessories: " + e.getMessage());
        }
    }

    /**
     * Loads all accessories from the CSV file.
     *
     * @return List of accessories, or an empty list if the file does not exist.
     */
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return accessories;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split(DELIMITER);
                String discriminator = data[0];

                String id = data[1];
                String title = data[2];
                double price = Double.parseDouble(data[3]);
                int stock = Integer.parseInt(data[4]);

                List<String> compatibleConsoles = new ArrayList<>();
                if (data.length > 5 && !data[5].isEmpty()) {
                    compatibleConsoles = Arrays.asList(data[5].split(LIST_DELIMITER));
                }

                switch (discriminator) {
                    case "CONTROLLER":
                        String connectionType = data[6];
                        accessories.add(new Controller(id, title, price, stock, compatibleConsoles, connectionType));
                        break;
                    case "CABLE":
                        double length = Double.parseDouble(data[6]);
                        String connectorType = data[7];
                        accessories.add(new Cable(id, title, price, stock, compatibleConsoles, length, connectorType));
                        break;
                    case "MEMORY":
                        int capacity = Integer.parseInt(data[6]);
                        String memoryType = data[7];
                        accessories.add(new Memory(id, title, price, stock, compatibleConsoles, capacity, memoryType));
                        break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading accessories: " + e.getMessage());
        }

        return accessories;
    }
}