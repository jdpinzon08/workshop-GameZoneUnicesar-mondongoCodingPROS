package com.gamezone.persistence;

import com.gamezone.model.Console;
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
            } else {
                Console console = (Console) product;

                line = "console,"
                        + console.getId() + ","
                        + console.getTitle() + ","
                        + console.getPrice() + ","
                        + console.getStockQuantity() + ",,,,,"
                        + console.getBrand() + ","
                        + console.getModel() + ","
                        + console.getGeneration();
            }

            lines.add(line);
        }

        try {
            Files.write(Path.of(filePath), lines);
        } catch (IOException e) {
            throw new RuntimeException("Error saving products", e);
        }
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
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading products", e);
        }

        return products;
    }
}