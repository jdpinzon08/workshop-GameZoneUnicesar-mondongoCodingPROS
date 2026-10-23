package com.gamezone.ui;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.GameZoneException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.Accessory;
import com.gamezone.service.PromotionService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Provides the interactive console interface for GameZone operations.
 */
public class ConsoleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final WarrantyService warrantyService;
    private final ReturnService returnService;
    private final PromotionService promotionService;
    private final Scanner scanner;

    /**
     * Creates the menu with core operational services.
     */
    public ConsoleMenu(ProductService productService, PersonService personService,
                       SaleService saleService, WarrantyService warrantyService) {
        this(productService, personService, saleService, warrantyService, null);
    }

    /**
     * Creates the menu with all available services.
     */
    public ConsoleMenu(ProductService productService, PersonService personService,
                       SaleService saleService, WarrantyService warrantyService,
                       ReturnService returnService) {
        this(productService, personService, saleService, warrantyService, returnService, null);
    }

    /** Creates the menu with all available services, including promotion management. */
    public ConsoleMenu(ProductService productService, PersonService personService,
                       SaleService saleService, WarrantyService warrantyService,
                       ReturnService returnService, PromotionService promotionService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.warrantyService = warrantyService;
        this.returnService = returnService;
        this.promotionService = promotionService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the main menu loop until the user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1": handleRegisterSale(); break;
                    case "2": handleListSales(); break;
                    case "3": handleListProducts(); break;
                    case "4": handleCheckWarranties(); break;
                    case "5": handleReturns(); break;
                    case "6": handleRegisterCategoryPromotion(); break;
                    case "0":
                        System.out.println("\nGracias por usar GameZone Unicesar. ¡Hasta pronto!");
                        running = false;
                        break;
                    default:
                        System.out.println("\n[!] Opción inválida. Por favor, seleccione una opción válida.");
                }
            } catch (Exception e) {
                handleException(e);
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n========================================");
        System.out.println("       GAMEZONE UNICESAR - MENÚ         ");
        System.out.println("========================================");
        System.out.println("1. Registrar una venta");
        System.out.println("2. Ver historial de ventas");
        System.out.println("3. Listar productos disponibles");
        System.out.println("4. Gestionar garantías");
        if (returnService != null) {
            System.out.println("5. Gestionar devoluciones");
        }
        if (promotionService != null) {
            System.out.println("6. Registrar promoción por categoría");
        }
        System.out.println("0. Salir");
        System.out.println("========================================");
        System.out.print("Seleccione una opción: ");
    }

    private void handleRegisterCategoryPromotion() {
        if (promotionService == null) {
            return;
        }
        System.out.println("\n=== registrar promoción por categoría ===");
        System.out.println("1. videojuegos");
        System.out.println("2. consolas");
        System.out.println("3. accesorios");
        System.out.print("seleccione la categoría: ");
        String option = scanner.nextLine().trim();
        String category;
        Class<?> targetType;
        switch (option) {
            case "1" -> { category = "VIDEOGAME"; targetType = com.gamezone.model.VideoGame.class; }
            case "2" -> { category = "CONSOLE"; targetType = Console.class; }
            case "3" -> { category = "ACCESSORY"; targetType = Accessory.class; }
            default -> {
                System.out.println("[!] categoría inválida.");
                return;
            }
        }
        try {
            System.out.print("identificador: ");
            String id = scanner.nextLine().trim();
            System.out.print("nombre: ");
            String name = scanner.nextLine().trim();
            System.out.print("fecha inicial (AAAA-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("fecha final (AAAA-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("porcentaje de descuento: ");
            double percentage = Double.parseDouble(scanner.nextLine().trim());
            CategoryDiscount promotion = new CategoryDiscount(id, name, startDate, endDate,
                    percentage, category, targetType, percentage);
            promotionService.registerCategoryDiscount(promotion);
            System.out.println("[✓] promoción registrada correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("[X] no se pudo registrar la promoción: " + e.getMessage());
        }
    }

    private void handleRegisterSale() {
        System.out.println("\n--- Registro de una Nueva Venta ---");
        System.out.print("Ingrese la cédula del comprador: ");
        String customerId = scanner.nextLine().trim();
        System.out.print("Ingrese la cédula del vendedor encargado: ");
        String sellerId = scanner.nextLine().trim();

        List<Product> selectedProducts = new ArrayList<>();
        List<String> extendedWarrantyProductIds = new ArrayList<>();
        boolean addingProducts = true;

        while (addingProducts) {
            System.out.print("Ingrese el identificador del producto: ");
            String pId = scanner.nextLine().trim();

            try {
                Product foundProduct = productService.findProductById(pId);
                if (foundProduct != null) {
                    selectedProducts.add(foundProduct);
                    System.out.println("-> Agregado: " + foundProduct.getTitle());

                    if (foundProduct instanceof Console) {
                        System.out.print("   ¿Desea agregar garantía extendida (10% extra) para esta consola? (s/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                            extendedWarrantyProductIds.add(foundProduct.getId());
                        }
                    }
                }
            } catch (ResourceNotFoundException e) {
                System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
            }

            System.out.print("¿Desea agregar otro producto a la compra? (s/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                addingProducts = false;
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("[X] Operación cancelada: No se seleccionaron productos.");
            return;
        }

        double totalAmount = 0.0;
        for (Product p : selectedProducts) {
            totalAmount += p.getPrice();
        }

        Customer customer = personService.findCustomerById(customerId);
        if (customer == null) {
            customer = new Customer(customerId, "Cliente " + customerId, "N/A", "cliente@gamezone.com");
        }

        Seller seller = personService.getAllSellers().stream()
                .filter(candidate -> candidate.getId().equalsIgnoreCase(sellerId))
                .findFirst()
                .orElse(null);

        if (seller == null) {
            seller = new Seller(sellerId, "Vendedor " + sellerId, "N/A", sellerId, "General");
        }

        String saleId = "venta-" + (saleService.getAllSales().size() + 1);
        String currentDate = LocalDate.now().toString();

        Sale sale = new Sale(saleId, selectedProducts, totalAmount, currentDate, customer, seller, "", 0.0);
        saleService.registerSale(sale, extendedWarrantyProductIds);

        System.out.println("\n[✓] ¡Venta registrada exitosamente!");
        System.out.println("Código de venta: " + sale.getSaleId());
        System.out.println("Total a pagar:   $" + sale.getTotalAmount());
    }

    private void handleReturns() {
        if (returnService == null) {
            System.out.println("La gestión de devoluciones está deshabilitada.");
            return;
        }

        System.out.println("\n=== Gestor de Devoluciones ===");
        System.out.println("1. Registrar una devolución");
        System.out.println("2. Mostrar historial de devoluciones");
        System.out.println("0. Volver");
        System.out.print("Seleccione una opción: ");
        String option = scanner.nextLine().trim();

        if ("1".equals(option)) {
            System.out.print("Ingrese el código de la venta: ");
            String saleId = scanner.nextLine().trim();
            System.out.print("Ingrese los códigos de los productos (separados por comas): ");
            String productIdsInput = scanner.nextLine().trim();

            List<String> productIds = new ArrayList<>();
            for (String productId : productIdsInput.split(",")) {
                if (!productId.isBlank()) {
                    productIds.add(productId.trim());
                }
            }

            System.out.print("Ingrese el motivo de la devolución: ");
            String reason = scanner.nextLine().trim();

            Return registeredReturn = returnService.registerReturn(saleId, productIds, reason);
            System.out.println(registeredReturn.generateReturnReceipt());

        } else if ("2".equals(option)) {
            List<Return> returns = returnService.viewAllReturns();
            if (returns.isEmpty()) {
                System.out.println("Aún no hay devoluciones registradas.");
            } else {
                returns.forEach(item -> System.out.println(item.generateReturnReceipt()));
            }
        }
    }

    private void handleListSales() {
        System.out.println("\n--- Historial de Ventas ---");
        List<Sale> sales = saleService.getAllSales();
        if (sales.isEmpty()) {
            System.out.println("Aún no hay ventas registradas.");
            return;
        }
        for (Sale s : sales) {
            System.out.println("Código: " + s.getSaleId() + " | Fecha: " + s.getDate() + " | Total: $" + s.getTotalAmount());
        }
    }

    private void handleListProducts() {
        System.out.println("\n--- Catálogo de Productos Disponibles ---");
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados en el sistema.");
            return;
        }
        for (Product p : products) {
            System.out.println("Código: " + p.getId() + " | Nombre: " + p.getTitle() + " | Precio: $" + p.getPrice() + " | Existencias: " + p.getStockQuantity());
        }
    }

    private void handleCheckWarranties() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Gestor de Garantías ===");
            System.out.println("1. Consultar garantía por producto y venta");
            System.out.println("2. Mostrar todas las garantías");
            System.out.println("3. Mostrar garantías activas");
            System.out.println("4. Mostrar garantías próximas a vencer");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    System.out.print("Ingrese el identificador del producto: ");
                    String pId = scanner.nextLine().trim();
                    System.out.print("Ingrese el código de la venta: ");
                    String sId = scanner.nextLine().trim();
                    Warranty w = warrantyService.findWarrantyByProduct(pId, sId);
                    System.out.println(w != null ? "\nGarantía encontrada: " + w.getId() : "\n[!] No se encontró ninguna garantía asociada.");
                    break;
                case "2":
                    warrantyService.listAllWarranties().forEach(item ->
                            System.out.println("Código: " + item.getId() + " | Vencimiento: " + item.getEndDate()));
                    break;
                case "3":
                    warrantyService.listActiveWarranties().forEach(item ->
                            System.out.println("Activa - Código: " + item.getId() + " | Vencimiento: " + item.getEndDate()));
                    break;
                case "4":
                    System.out.print("Ingrese el margen de días para vencimiento: ");
                    try {
                        int days = Integer.parseInt(scanner.nextLine().trim());
                        warrantyService.listWarrantiesExpiringSoon(days).forEach(item ->
                                System.out.println("Próxima a expirar - Código: " + item.getId() + " | Vencimiento: " + item.getEndDate()));
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Debe ingresar un número entero válido.");
                    }
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("\n[!] Opción no reconocida. Intente nuevamente.");
            }
        }
    }

    /**
     * Differentiated exception handler providing contextual messages without exposing raw stack traces.
     */
    private void handleException(Exception e) {
        if (e instanceof ResourceNotFoundException) {
            System.out.println("\n[!] No se encontró el recurso solicitado: " + e.getMessage());
        } else if (e instanceof BusinessRuleException) {
            System.out.println("\n[X] No se puede completar la operación: " + e.getMessage());
        } else if (e instanceof InvalidDataException) {
            System.out.println("\n[!] Los datos ingresados no son válidos: " + e.getMessage());
        } else if (e instanceof PersistenceException) {
            System.out.println("\n[ERROR] Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } else if (e instanceof GameZoneException) {
            System.out.println("\n[ERROR GameZone] " + e.getMessage());
        } else {
            System.out.println("\n[ERROR INESPERADO] Ocurrió un error inesperado en el sistema.");
        }
    }
}
