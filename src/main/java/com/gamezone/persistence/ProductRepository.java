package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private String filePath;

    /**
     * Creates a product repository using the specified file path.
     *
     * @param filePath path where product data will be stored
     */
    public ProductRepository(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * Creates a product repository using the default CSV file.
     */
    public ProductRepository() {
        this("data/product.csv");
    }

    /**
     * Creates the data directory and CSV file if they do not exist.
     */
    private void ensureFileExists() {
        try {
            Path path = Path.of(filePath);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error creating products file", e);
        }
    }

    /**
     * Saves all products to the CSV file.
     *
     * @param products list of products to save
     */
    public void saveAll(List<Product> products) {
        List<String> lines = new ArrayList<>();

        lines.add("type,id,title,price,stockQuantity,platform,genre,ageRating,brand,model,generation");

        for (Product product : products) {
            String line;

            if (product instanceof VideoGame) {
                VideoGame videoGame = (VideoGame) product;

                line = "videoGame,"
                        + videoGame.getId() + ","
                        + videoGame.getTitle() + ","
                        + videoGame.getPrice() + ","
                        + videoGame.getStockQuantity() + ","
                        + videoGame.getPlatform() + ","
                        + videoGame.getGenre() + ","
                        + videoGame.getAgeRating() + ",,,";
            } else if (product instanceof Console) {
                Console console = (Console) product;

                line = "console,"
                        + console.getId() + ","
                        + console.getTitle() + ","
                        + console.getPrice() + ","
                        + console.getStockQuantity() + ",,,,,"
                        + console.getBrand() + ","
                        + console.getModel() + ","
                        + console.getGeneration();
            } else if (product instanceof Controller) {
                Controller controller = (Controller) product;
                line = accessoryLine("controller", controller,
                        String.join(";", controller.getCompatibleConsoles()),
                        controller.getConnectionType(), "");
            } else if (product instanceof Cable) {
                Cable cable = (Cable) product;
                line = accessoryLine("cable", cable,
                        String.join(";", cable.getCompatibleConsoles()),
                        Double.toString(cable.getLengthInMeters()), cable.getConnectorType());
            } else if (product instanceof Memory) {
                Memory memory = (Memory) product;
                line = accessoryLine("memory", memory,
                        String.join(";", memory.getCompatibleConsoles()),
                        Integer.toString(memory.getCapacityInGB()), memory.getMemoryType());
            } else {
                throw new IllegalArgumentException("Unsupported product type: " + product.getClass().getName());
            }

            lines.add(line);
        }

        try {
            Files.write(Path.of(filePath), lines);
        } catch (IOException e) {
            throw new RuntimeException("Error saving products", e);
        }
    }

    private String accessoryLine(String type, Accessory accessory, String compatibleConsoles,
                                 String detail1, String detail2) {
        return type + "," + accessory.getId() + "," + accessory.getTitle() + ","
                + accessory.getPrice() + "," + accessory.getStockQuantity() + ","
                + compatibleConsoles + "," + detail1 + "," + detail2 + ",,,";
    }

    /**
     * Loads all products stored in the CSV file.
     *
     * @return list of products read from the file
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                String type = parts[0];
                String id = parts[1];
                String title = parts[2];
                double price = Double.parseDouble(parts[3]);
                int stockQuantity = Integer.parseInt(parts[4]);

                if ("videoGame".equalsIgnoreCase(type)) {
                    String platform = parts[5];
                    String genre = parts[6];
                    String ageRating = parts[7];

                    products.add(new VideoGame(id, title, price, stockQuantity, platform, genre, ageRating));

                } else if ("console".equalsIgnoreCase(type)) {
                    String brand = parts[8];
                    String model = parts[9];
                    String generation = parts[10];

                    products.add(new Console(id, title, price, stockQuantity, brand, model, generation));
                } else if ("controller".equalsIgnoreCase(type)
                        || "cable".equalsIgnoreCase(type)
                        || "memory".equalsIgnoreCase(type)) {
                    List<String> compatibleConsoles = parts[5].isEmpty()
                            ? new ArrayList<>() : List.of(parts[5].split(";", -1));
                    switch (type.toLowerCase()) {
                        case "controller":
                            products.add(new Controller(id, title, price, stockQuantity,
                                    compatibleConsoles, parts[6]));
                            break;
                        case "cable":
                            products.add(new Cable(id, title, price, stockQuantity,
                                    compatibleConsoles, Double.parseDouble(parts[6]), parts[7]));
                            break;
                        case "memory":
                            products.add(new Memory(id, title, price, stockQuantity,
                                    compatibleConsoles, Integer.parseInt(parts[6]), parts[7]));
                            break;
                        default:
                            throw new IllegalStateException("Unhandled accessory type: " + type);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading products", e);
        }

        return products;
    }
}
