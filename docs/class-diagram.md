# Class Diagram

```mermaid
classDiagram
    class Person {
        <<abstract>>
        -String id
        -String name
        -String phoneNumber
    }
    class Customer {
        -String email
        -List~Sale~ purchaseHistory
    }
    class Seller {
        -String employeeId
        -String workShift
    }
    Person <|-- Customer
    Person <|-- Seller

    class Product {
        <<abstract>>
        -String id
        -String title
        -double price
        -int stockQuantity
    }
    class VideoGame {
        -String platform
        -String genre
        -String ageRating
    }
    class Console {
        -String brand
        -String model
        -String generation
    }
    class Accessory {
        <<abstract>>
        -List~String~ compatibleConsoles
    }
    class Controller {
        -String connectionType
    }
    class Cable {
        -double lengthInMeters
        -String connectorType
    }
    class Memory {
        -int capacityInGB
        -String memoryType
    }
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    class Sale {
        -String saleId
        -List~Product~ products
        -double totalAmount
        -String date
        -Customer customer
        -Seller seller
        +getSaleId() String
        +calculateTotal() double
    }
    class Return {
        -String id
        -LocalDate returnDate
        -Sale sale
        -List~Product~ returnedProducts
        -String reason
        -double refundAmount
    }
    class Warranty {
        <<abstract>>
        -String id
        -Product product
        -Sale sale
        -LocalDate startDate
        -LocalDate endDate
    }
    class BasicWarranty
    class ExtendedWarranty
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty
    Sale "0..*" --> "1" Customer
    Sale "0..*" --> "1" Seller
    Sale "1" o-- "1..*" Product
    Return "0..*" --> "1" Sale
    Return "0..*" o-- "1..*" Product
    Warranty "0..*" --> "1" Product
    Warranty "0..*" --> "1" Sale

    class ProductRepository {
        +saveAll(List~Product~) void
        +findAll() List~Product~
    }
    class AccessoryRepository {
        +saveAll(List~Accessory~) void
        +loadAll() List~Accessory~
    }
    class SaleRepository {
        +saveAll(List~Sale~) void
        +findAll(products, customers, sellers) List~Sale~
    }
    class PersonRepository {
        +saveAll(List~Person~) void
        +findAll() List~Person~
    }
    class ReturnRepository {
        +saveAll(List~Return~) void
        +loadAll() List~Return~
    }
    class WarrantyRepository {
        +saveAll(List~Warranty~) void
        +loadAll() List~Warranty~
    }

    class ProductService
    class AccessoryService
    class SaleService {
        +registerSale(Sale, List~String~) void
        +getSaleById(String saleId) Sale
        +getAllSales() List~Sale~
        +getSalesByCustomer(String customerId) List~Sale~
        +getSalesBySeller(String sellerId) List~Sale~
    }
    class ReturnService {
        +registerReturn(String saleId, List~String~ productIds, String reason) Return
    }
    class WarrantyService
    class PersonService
    class ConsoleMenu
    class Main

    ProductService --> ProductRepository
    AccessoryService --> AccessoryRepository
    PersonService --> PersonRepository
    AccessoryRepository --> ProductRepository : shared product CSV
    SaleService --> SaleRepository
    SaleService --> ProductService
    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ProductService
    ReturnRepository --> SaleService : getSaleById(saleId)
    ReturnRepository --> ProductService : resolve productId
    WarrantyService --> WarrantyRepository
    WarrantyRepository --> SaleService : getSaleById(saleId)
    WarrantyRepository --> ProductService
    SaleService ..> WarrantyService : warranty assignment
    ConsoleMenu --> SaleService
    ConsoleMenu --> ProductService
    ConsoleMenu --> ReturnService
    ConsoleMenu --> WarrantyService
    Main --> ProductService
    Main --> AccessoryService
    Main --> SaleService
    Main --> ReturnService
    Main --> WarrantyService
```

`SaleRepository` keeps the original five-field sales rows readable and writes `totalAmount` as an optional sixth field. On load it resolves product, customer, and seller IDs to the domain objects already loaded by their services. Accessories share `product.csv` with the other product types.
