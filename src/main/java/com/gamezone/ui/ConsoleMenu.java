package com.gamezone.ui;

import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.WarrantyService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Provides the interactive console interface for GameZone operations. */
public class ConsoleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final WarrantyService warrantyService;
    private final ReturnService returnService;
    private final Scanner scanner;

    /** Creates the menu with product, person, sale, and warranty services. */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService, WarrantyService warrantyService) {
        this(productService, personService, saleService, warrantyService, null);
    }

    /** Creates the menu with all available services. */
    public ConsoleMenu(ProductService productService, PersonService personService,
                       SaleService saleService, WarrantyService warrantyService,
                       ReturnService returnService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.warrantyService = warrantyService;
        this.returnService = returnService;
        this.scanner = new Scanner(System.in);
    }

    /** Displays the main menu until the user exits. */
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
                case "5": handleReturns(); break;
                case "0":
                    System.out.println("\nGracias por usar gamezone unicesar, vuelve pronto mi loco");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] opcion invalida, usa una buena.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n========================================");
        System.out.println("       GAMEZONE UNICESAR - MENU         ");
        System.out.println("========================================");
        System.out.println("1. registra una venta ");
        System.out.println("2. ver historial de ventas ");
        System.out.println("3. listar los productos disponibles");
        System.out.println("4. revisar las ventar y productos en garantia");
        if (returnService != null) {
            System.out.println("5. gestionar devoluciones");
        }
        System.out.println("0. salir");
        System.out.println("========================================");
        System.out.print("Select an option: ");
    }

    private void handleRegisterSale() {
        System.out.println("\n ____registro de una nueva venta____");
        System.out.print("ingrese la cedula del comprador: ");
        String customerId = scanner.nextLine().trim();
        System.out.print("ingrese la cedula del vendedor encargado: ");
        String sellerId = scanner.nextLine().trim();

        List<Product> selectedProducts = new ArrayList<>();
        List<String> extendedWarrantyProductIds = new ArrayList<>();
        boolean addingProducts = true;

        while (addingProducts) {
            System.out.print("ingrese el identificador del producto: ");
            String pId = scanner.nextLine().trim();
            Product foundProduct = productService.findProductById(pId);

            if (foundProduct != null) {
                selectedProducts.add(foundProduct);
                System.out.println("-> agregado: " + foundProduct.getTitle());

                if (foundProduct instanceof Console) {
                    System.out.print("   agregar garantia (10% extra) para esta consola? (s/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                        extendedWarrantyProductIds.add(foundProduct.getId());
                    }
                }
            } else {
                System.out.println("[!] no se ha encontrado un producto con ese identificador: " + pId);
            }

            System.out.print("desea agregar otro producto a la compra? (s/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                addingProducts = false;
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("[X] venta cancelada: no se eligieron productos.");
            return;
        }

        double totalAmount = 0.0;
        for (Product p : selectedProducts) {
            totalAmount += p.getPrice();
        }

        Customer customer = personService.findCustomerById(customerId);
        if (customer == null) {
            customer = new Customer(customerId, "cliente: " + customerId, "N/A", "N/A");
        }
        Seller seller = personService.getAllSellers().stream()
                .filter(candidate -> candidate.getId().equalsIgnoreCase(sellerId))
                .findFirst().orElse(null);
        if (seller == null) {
            seller = new Seller(sellerId, "vendedor " + sellerId, "N/A", sellerId, "Day");
        }
        String saleId = "venta-" + (saleService.getAllSales().size() + 1);
        String currentDate = LocalDate.now().toString();

        try {
            Sale sale = new Sale(saleId, selectedProducts, totalAmount, currentDate, customer, seller, "", 0.0);
            saleService.registerSale(sale, extendedWarrantyProductIds);

            System.out.println("\n[✓] venta registrada correctamente!");
            System.out.println("codigo de la venta: " + sale.getSaleId());
            System.out.println("costo total: $" + sale.getTotalAmount());
        } catch (IllegalArgumentException e) {
            System.out.println("\n[X] error en realizacion de la venta, intente nuevamente: " + e.getMessage());
        }
    }

    private void handleReturns() {
        if (returnService == null) {
            System.out.println("la gestion de las devoluciones esta deshabilitada.");
            return;
        }
        System.out.println("\n=== gestor de devoluciones ===");
        System.out.println("1. registrar una devolucion");
        System.out.println("2. mostrar devoluciones");
        System.out.println("0. volver");
        System.out.print("seleccione una opcion: ");
        String option = scanner.nextLine().trim();
        if ("1".equals(option)) {
            System.out.print("ingrese el codigo de la venta: ");
            String saleId = scanner.nextLine().trim();
            System.out.print("ingrese el codigo de los productos (separados por comas): ");
            String productIdsInput = scanner.nextLine().trim();
            List<String> productIds = new ArrayList<>();
            for (String productId : productIdsInput.split(",")) {
                if (!productId.isBlank()) {
                    productIds.add(productId.trim());
                }
            }
            System.out.print("ingrese la razon de la devolucion: ");
            String reason = scanner.nextLine().trim();
            try {
                Return registeredReturn = returnService.registerReturn(saleId, productIds, reason);
                System.out.println(registeredReturn.generateReturnReceipt());
            } catch (IllegalArgumentException e) {
                System.out.println("[X] error procesando la devolucion, intente nuevamente : " + e.getMessage());
            }
        } else if ("2".equals(option)) {
            List<Return> returns = returnService.viewAllReturns();
            if (returns.isEmpty()) {
                System.out.println("aun no hay devoluciones registradas.");
            } else {
                returns.forEach(item -> System.out.println(item.generateReturnReceipt()));
            }
        }
    }

    private void handleListSales() {
        System.out.println("\n___ historial de ventas ___");
        List<Sale> sales = saleService.getAllSales();
        if (sales.isEmpty()) {
            System.out.println("aun no hay ventas registradas.");
            return;
        }
        for (Sale s : sales) {
            System.out.println("codigo: " + s.getSaleId() + " | fecha: " + s.getDate() + " | Total: $" + s.getTotalAmount());
        }
    }

    private void handleListProducts() {
        System.out.println("\n___ catalogo de productos disponibles ___");
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("no se han encontrado productos.");
            return;
        }
        for (Product p : products) {
            System.out.println("codigo: " + p.getId() + " | nombre: " + p.getTitle() + " | costo: $" + p.getPrice() + " | cantidad disponible: " + p.getStockQuantity());
        }
    }

    private void handleCheckWarranties() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== gestor de garantias ===");
            System.out.println("1. encontrar garantia por producto y venta");
            System.out.println("2. mostrar todas las garantias");
            System.out.println("3. mostrar las garantias vigentes");
            System.out.println("4. mostrar garantias a punto de expirar");
            System.out.println("0. volver al menu principal");
            System.out.print("seleccione una opcion: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    System.out.print("ingrese el codigo del producto: ");
                    String pId = scanner.nextLine().trim();
                    System.out.print("ingrese el codigo de la venta: ");
                    String sId = scanner.nextLine().trim();
                    Warranty w = warrantyService.findWarrantyByProduct(pId, sId);
                    System.out.println(w != null ? "\ngarantia encontrada: " + w.getId() : "\n[!] no se encontro garantia.");
                    break;
                case "2":
                    warrantyService.listAllWarranties().forEach(item -> System.out.println("codigo: " + item.getId() + " | finaliza: " + item.getEndDate()));
                    break;
                case "3":
                    warrantyService.listActiveWarranties().forEach(item -> System.out.println("Activa - codigo: " + item.getId() + " | finaliza: " + item.getEndDate()));
                    break;
                case "4":
                    System.out.print("proximos a expirar: ");
                    try {
                        int days = Integer.parseInt(scanner.nextLine().trim());
                        warrantyService.listWarrantiesExpiringSoon(days).forEach(item -> System.out.println("expirada - codigo: " + item.getId() + " | finalizo: " + item.getEndDate()));
                    } catch (NumberFormatException e) {
                        System.out.println("[!] numero invalido.");
                    }
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("\n[!] numero invalido mi hermano, haga algo bien.");
            }
        }
    }
}
