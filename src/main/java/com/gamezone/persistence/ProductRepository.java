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

    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

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
    }
}