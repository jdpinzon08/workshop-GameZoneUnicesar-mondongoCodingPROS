package com.gamezone.persistence;

import com.gamezone.model.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *the repository of promotions
 */
public class PromotionRepository {
    private final String filePath;

    /**
     * constructor of the filepath
     * @param filePath will create a file with the data
     */
    public PromotionRepository(String filePath){
        this.filePath=filePath;
    }

    /**
     * save the new promotion that will be created
     * @param promotions the list of promotions that are crated
     */
    public void saveAll(List<Promotion> promotions){
        try {
            Path path = Path.of(filePath);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            List<String> lines = new ArrayList<>();

            for(Promotion promotion : promotions){
                String type= "";

                if (promotion instanceof CategoryDiscount) {
                    type = "CategoryDiscount";
                } else if (promotion instanceof PercentageDiscount) {
                    type = "PercentageDiscount";
                } else if(promotion instanceof BulkPurchaseDiscount){
                    type= "BulkPurchaseDiscount";
                }
                lines.add(
                        type + "," +
                                promotion.getId() + "," +
                                promotion.getName() + "," +
                                promotion.getStartDate() + "," +
                                promotion.getEndDate()
                );
            }
            Files.write(path, lines);

        } catch (IOException e){
            throw new RuntimeException("Error saving promotion.", e);
        }
    }


    /**
     * load and return the promotions previously saved or the empty list if not exist promotions saved
     * @return the list of promotions saved
     */
    public List<Promotion> loadAll(){
        List<Promotion> promotionsList = new ArrayList<>();
        Path path = Path.of(filePath);

        //validacion por si esta vacia, asi devuelve una lista vacia

        if (!Files.exists(path)) {
            return promotionsList;
        }

        try{
            List<String> lines = Files.readAllLines(path);
            for(String line : lines){

                if(line.isBlank()){
                    continue;
                }
                String[] parts = line.split(",", -1);

                if (parts.length != 5) {
                    continue;
                }

                String type =parts[0];
                String id =parts[1];
                String name =parts[2];
                LocalDate initialDate=LocalDate.parse(parts[3]);
                LocalDate endDate= LocalDate.parse(parts[4]);

            }
        } catch (Exception e) {
            throw new RuntimeException("error loading the promotions"+ e);
        }
        return promotionsList;
    }

}
