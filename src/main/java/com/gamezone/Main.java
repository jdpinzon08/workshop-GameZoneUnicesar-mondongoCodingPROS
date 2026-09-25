package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.ConsoleMenu;

/** Starts GameZone and connects its repositories, services, and console menu. */
public class Main {
    /** Builds the application components and starts the console interface. */
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        AccessoryRepository accessoryRepository = new AccessoryRepository(productRepository);
        PersonRepository personRepository = new PersonRepository("data/persons.csv");
        SaleRepository saleRepository = new SaleRepository();

        ProductService productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository, productService);
        SaleService saleService = new SaleService(saleRepository, productService, personService);

        WarrantyRepository warrantyRepository = new WarrantyRepository(productService, saleService);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository);
        saleService.setWarrantyService(warrantyService);

        ReturnRepository returnRepository = new ReturnRepository(saleService, productService);
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService);

        ConsoleMenu consoleMenu = new ConsoleMenu(productService, personService, saleService,
                warrantyService, returnService);
        consoleMenu.start();
    }
}
