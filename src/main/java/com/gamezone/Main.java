package com.gamezone;

import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        PersonRepository personRepository = new PersonRepository("persons.csv");
        SaleRepository saleRepository = new SaleRepository();

        ProductService productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        SaleService saleService = new SaleService(saleRepository, productService);

        // 1. Instanciar garantías pasando los servicios que necesita
        WarrantyRepository warrantyRepository = new WarrantyRepository(productService, saleService);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository);

        // 2. Resolver dependencia circular inyectando el servicio de garantías en ventas
        saleService.setWarrantyService(warrantyService);

        ConsoleMenu consoleMenu = new ConsoleMenu(productService, personService, saleService, warrantyService);
        consoleMenu.start();
    }
}