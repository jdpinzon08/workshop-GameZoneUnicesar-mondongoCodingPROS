package com.gamezone;

import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.ConsoleMenu;

/**
 * Main application entry point for GameZone Unicesar.
 * Initializes core persistence layers, injects business services,
 * and launches the primary user interface handler.
 *
 * @author Technical Lead (mondongoCodingPROS)
 * @version 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        // Instantiate repositories with required file path parameters
        ProductRepository productRepository = new ProductRepository();
        PersonRepository personRepository = new PersonRepository("persons.csv");
        SaleRepository saleRepository = new SaleRepository();

        // Instantiate business service layer dependencies
        ProductService productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        SaleService saleService = new SaleService(saleRepository, productService);

        // Launch the text-based console user interface
        ConsoleMenu consoleMenu = new ConsoleMenu(productService, personService, saleService);
        consoleMenu.start();
    }
}