package com.gamezone.ui;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Text-based User Interface component for GameZone Unicesar.
 * Handles interactive menu execution, user inputs, and service delegation.
 *
 * @author Technical Lead (mondongoCodingPROS)
 * @version 1.0.0
 */
public class ConsoleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final Scanner scanner;

    /**
     * Constructs a ConsoleUI instance with the required application services.
     *
     * @param productService Product business service.
     * @param personService  Person business service.
     * @param saleService    Sale business service.
     */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the main menu loop for user interaction.
     */
    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    handleRegisterSale();
                    break;
                case "2":
                    handleListSales();
                    break;
                case "3":
                    handleListProducts();
                    break;
                case "0":
                    System.out.println("\nThank you for using GameZone Unicesar! Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid option. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n========================================");
        System.out.println("       GAMEZONE UNICESAR - MENU         ");
        System.out.println("========================================");
        System.out.println("1. Register new sale");
        System.out.println("2. View sales transaction history");
        System.out.println("3. List available products");
        System.out.println("0. Exit");
        System.out.println("========================================");
        System.out.print("Select an option: ");
    }

    private void handleRegisterSale() {
        System.out.println("\n--- NEW SALE REGISTRATION ---");
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine().trim();

        System.out.print("Enter Seller ID: ");
        String sellerId = scanner.nextLine().trim();

        List<Product> selectedProducts = new ArrayList<>();
        boolean addingProducts = true;

        while (addingProducts) {
            System.out.print("Enter Product ID to purchase: ");
            String pId = scanner.nextLine().trim();

            Product foundProduct = productService.findProductById(pId);

            if (foundProduct != null) {
                selectedProducts.add(foundProduct);
                System.out.println("-> Added: " + foundProduct.getTitle());
            } else {
                System.out.println("[!] Product not found with ID: " + pId);
            }

            System.out.print("Do you want to add another product? (y/n): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (!ans.equals("y")) {
                addingProducts = false;
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("[X] Sale cancelled: No products selected.");
            return;
        }

        // Calculate total amount from selected products
        double totalAmount = 0.0;
        for (Product p : selectedProducts) {
            totalAmount += p.getPrice();
        }

        Customer customer = new Customer(customerId, "Customer " + customerId, "N/A", "N/A");
        Seller seller = new Seller(sellerId, "Seller " + sellerId, "N/A", sellerId, "Day");
        String saleId = "SALE-" + (saleService.getAllSales().size() + 1);
        String currentDate = LocalDate.now().toString();

        try {
            // Constructor with 6 parameters (id, date, totalAmount, customer, seller, products)
            Sale sale = new Sale(saleId, selectedProducts, totalAmount, currentDate, customer, seller);
            saleService.registerSale(sale);

            System.out.println("\n[✓] Sale registered successfully!");
            System.out.println("Sale ID: " + sale.getSaleId());
            System.out.println("Total Paid: $" + sale.getTotalAmount());
        } catch (IllegalArgumentException e) {
            System.out.println("\n[X] Error processing sale: " + e.getMessage());
        }
    }

    private void handleListSales() {
        System.out.println("\n--- TRANSACTION HISTORY ---");
        List<Sale> sales = saleService.getAllSales();
        if (sales.isEmpty()) {
            System.out.println("No sales recorded yet.");
            return;
        }

        for (Sale s : sales) {
            System.out.println("ID: " + s.getSaleId() + " | Date: " + s.getDate()
                    + " | Total: $" + s.getTotalAmount());
        }
    }

    private void handleListProducts() {
        System.out.println("\n--- PRODUCT CATALOG ---");
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (Product p : products) {
            System.out.println("ID: " + p.getId() + " | Title: " + p.getTitle()
                    + " | Price: $" + p.getPrice() + " | Stock: " + p.getStockQuantity());
        }
    }
}