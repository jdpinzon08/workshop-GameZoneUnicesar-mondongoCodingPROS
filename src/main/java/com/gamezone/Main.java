package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.*;
import com.gamezone.ui.ConsoleMenu;

/** Starts GameZone and connects its repositories, services, and console menu. */
public class Main {
    /** Builds the application components and starts the console interface. */
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        AccessoryRepository accessoryRepository = new AccessoryRepository(productRepository);
        PersonRepository personRepository = new PersonRepository("data/persons.csv");
        SaleRepository saleRepository = new SaleRepository();
        PromotionService promotionService = new PromotionService(
                new PromotionRepository("data/promotions.csv"), java.util.List.of());

        ProductService productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository, productService);
        SaleService saleService = new SaleService(saleRepository, productService, personService,
                accessoryService);
        saleService.setPromotionService(promotionService);

        WarrantyRepository warrantyRepository = new WarrantyRepository("data/warranties.csv");
        WarrantyService warrantyService = new WarrantyService(
                warrantyRepository, saleRepository, productService);
        saleService.setWarrantyService(warrantyService);

        ReturnRepository returnRepository = new ReturnRepository(saleService, productService);
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService);

        ConsoleMenu consoleMenu = new ConsoleMenu(productService, personService, saleService,
                warrantyService, returnService, promotionService);
        consoleMenu.start();
    }
}
