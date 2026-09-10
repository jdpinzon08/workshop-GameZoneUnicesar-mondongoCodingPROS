package com.gamezone.persistence;
import com.gamezone.model.Sale;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class SaleRepository {
//el lugar donde se guardara todo ese locurero
private String filePath;

//constructor aca
public SaleRepository(String filePath){
    this.filePath=filePath;
    ensureFileExists();
}
//creador del csv en data
public SaleRepository() {
    this("data/sales.csv");
}
//pa que esa vaina funcione ome
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

    // Método para guardar todos los registros
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
    // Método para leer todos los registros del archivo
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

    private String joinProductIds(Sale sale) {
        List<String> ids = new ArrayList<>();
        sale.getProducts().forEach(p -> ids.add(p.getId()));
        return String.join("|", ids);
    }
}





