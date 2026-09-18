package com.gamezone.ui;

import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final WarrantyService warrantyService;
    private final Scanner scanner;

    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService, WarrantyService warrantyService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.warrantyService = warrantyService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1": handleRegisterSale(); break;
                case "2": handleListSales(); break;
                case "3": handleListProducts(); break;
                case "4": handleCheckWarranties(); break;
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
        System.out.println("4. Check product and sales warranties");
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
        List<String> extendedWarrantyProductIds = new ArrayList<>();
        boolean addingProducts = true;

        while (addingProducts) {
            System.out.print("Enter Product ID to purchase: ");
            String pId = scanner.nextLine().trim();
            Product foundProduct = productService.findProductById(pId);

            if (foundProduct != null) {
                selectedProducts.add(foundProduct);
                System.out.println("-> Added: " + foundProduct.getTitle());

                if (foundProduct instanceof Console) {
                    System.out.print("   Add Extended Warranty (10% extra) for this console? (y/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                        extendedWarrantyProductIds.add(foundProduct.getId());
                    }
                }
            } else {
                System.out.println("[!] Product not found with ID: " + pId);
            }

            System.out.print("Do you want to add another product? (y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                addingProducts = false;
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("[X] Sale cancelled: No products selected.");
            return;
        }

        double totalAmount = 0.0;
        for (Product p : selectedProducts) {
            totalAmount += p.getPrice();
        }

        Customer customer = new Customer(customerId, "Customer " + customerId, "N/A", "N/A");
        Seller seller = new Seller(sellerId, "Seller " + sellerId, "N/A", sellerId, "Day");
        String saleId = "SALE-" + (saleService.getAllSales().size() + 1);
        String currentDate = LocalDate.now().toString();

        try {
            Sale sale = new Sale(saleId, selectedProducts, totalAmount, currentDate, customer, seller);
            saleService.registerSale(sale, extendedWarrantyProductIds);

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
            System.out.println("ID: " + s.getSaleId() + " | Date: " + s.getDate() + " | Total: $" + s.getTotalAmount());
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
            System.out.println("ID: " + p.getId() + " | Title: " + p.getTitle() + " | Price: $" + p.getPrice() + " | Stock: " + p.getStockQuantity());
        }
    }

    private void handleCheckWarranties() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== WARRANTY MANAGEMENT ===");
            System.out.println("1. Find warranty by product and sale");
            System.out.println("2. List all warranties");
            System.out.println("3. List active warranties");
            System.out.println("4. List warranties expiring soon");
            System.out.println("0. Back to main menu");
            System.out.print("Select an option: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    System.out.print("Enter Product ID: ");
                    String pId = scanner.nextLine().trim();
                    System.out.print("Enter Sale ID: ");
                    String sId = scanner.nextLine().trim();
                    Warranty w = warrantyService.findWarrantyByProduct(pId, sId);
                    System.out.println(w != null ? "\nWarranty found: " + w.getId() : "\n[!] No warranty found.");
                    break;
                case "2":
                    warrantyService.listAllWarranties().forEach(item -> System.out.println("ID: " + item.getId() + " | End: " + item.getEndDate()));
                    break;
                case "3":
                    warrantyService.listActiveWarranties().forEach(item -> System.out.println("Active - ID: " + item.getId() + " | End: " + item.getEndDate()));
                    break;
                case "4":
                    System.out.print("Enter days ahead: ");
                    try {
                        int days = Integer.parseInt(scanner.nextLine().trim());
                        warrantyService.listWarrantiesExpiringSoon(days).forEach(item -> System.out.println("Expiring - ID: " + item.getId() + " | End: " + item.getEndDate()));
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Invalid number.");
                    }
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("\n[!] Invalid option.");
            }
        }
    }
}