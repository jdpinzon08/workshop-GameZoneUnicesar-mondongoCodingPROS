
package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.Console;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.model.VideoGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for saving and loading promotions.
 */
public class PromotionRepository {
    private final String filePath;

    /**
     * Creates a repository for the specified file.
     *
     * @param filePath path used to store promotion data
     */
    public PromotionRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all promotions to the CSV file.
     *
     * @param promotions promotions to persist
     */
    public void saveAll(List<Promotion> promotions) {
        try {
            Path path = Path.of(filePath);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            List<String> lines = new ArrayList<>();

            for (Promotion promotion : promotions) {
                if (promotion instanceof CategoryDiscount category) {
                    lines.add(String.join(",",
                            "CategoryDiscount",
                            category.getId(),
                            category.getName(),
                            category.getStartDate().toString(),
                            category.getEndDate().toString(),
                            category.getObjectCategory(),
                            Double.toString(category.getPercentageDiscount())
                    ));
                } else if (promotion instanceof PercentageDiscount percentage) {
                    lines.add(String.join(",",
                            "PercentageDiscount",
                            percentage.getId(),
                            percentage.getName(),
                            percentage.getStartDate().toString(),
                            percentage.getEndDate().toString(),
                            Double.toString(percentage.getPercentage())
                    ));
                } else if (promotion instanceof BulkPurchaseDiscount bulk) {
                    lines.add(String.join(",",
                            "BulkPurchaseDiscount",
                            bulk.getId(),
                            bulk.getName(),
                            bulk.getStartDate().toString(),
                            bulk.getEndDate().toString(),
                            Integer.toString(bulk.getMinimumQuantity()),
                            Double.toString(bulk.getDiscountPercentage())
                    ));
                }
            }

            Files.write(path, lines);
        } catch (IOException e) {
            throw new RuntimeException("Error saving promotions.", e);
        }
    }

    /**
     * Loads promotions from the CSV file.
     *
     * @return the promotions loaded from the file
     */
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            return promotions;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                String type = parts[0];

                switch (type) {
                    case "CategoryDiscount" -> {
                        if (parts.length != 7) {
                            throw new IllegalArgumentException(
                                    "Invalid CategoryDiscount record: " + line);
                        }

                        String category = parts[5].trim().toUpperCase();
                        Class<?> targetType = switch (category) {
                            case "ACCESSORY" -> Accessory.class;
                            case "CONSOLE" -> Console.class;
                            case "VIDEOGAME" -> VideoGame.class;
                            default -> throw new IllegalArgumentException(
                                    "Unsupported promotion category: " + category);
                        };

                        double percentage = Double.parseDouble(parts[6]);

                        promotions.add(new CategoryDiscount(
                                parts[1],
                                parts[2],
                                LocalDate.parse(parts[3]),
                                LocalDate.parse(parts[4]),
                                0.0,
                                category,
                                targetType,
                                percentage
                        ));
                    }
                    case "PercentageDiscount" -> {
                        if (parts.length != 6) {
                            throw new IllegalArgumentException(
                                    "Invalid PercentageDiscount record: " + line);
                        }

                        promotions.add(new PercentageDiscount(
                                parts[1],
                                parts[2],
                                LocalDate.parse(parts[3]),
                                LocalDate.parse(parts[4]),
                                Double.parseDouble(parts[5])
                        ));
                    }
                    case "BulkPurchaseDiscount" -> {
                        if (parts.length != 7) {
                            throw new IllegalArgumentException(
                                    "Invalid BulkPurchaseDiscount record: " + line);
                        }

                        int minimumQuantity = Integer.parseInt(parts[5]);
                        double percentage = Double.parseDouble(parts[6]);

                        promotions.add(new BulkPurchaseDiscount(
                                parts[1],
                                parts[2],
                                LocalDate.parse(parts[3]),
                                LocalDate.parse(parts[4]),
                                minimumQuantity,
                                0.0,
                                Accessory.class,
                                percentage
                        ));
                    }
                    default -> throw new IllegalArgumentException(
                            "Unknown promotion type: " + type);
                }
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Error loading promotions.", e);
        }

        return promotions;
    }
}
