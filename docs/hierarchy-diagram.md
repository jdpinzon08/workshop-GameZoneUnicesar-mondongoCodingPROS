# Inheritance Hierarchy Diagram

```mermaid
classDiagram
    %% Person Hierarchy
    class Person {
        <<abstract>>
        #String id
        #String name
        #String phoneNumber
    }

    class Customer {
        -String email
    }

    class Seller {
        -String employeeId
        -String workShift
    }

    Person <|-- Customer : extends
    Person <|-- Seller : extends

    %% Product Hierarchy
    class Product {
        <<abstract>>
        #String id
        #String title
        #double price
        #int stockQuantity
        +getDescription()* String
    }

    class VideoGame {
        -String platform
        -String genre
        -String ageRating
        +getDescription() String
    }

    class Console {
        -String brand
        -String model
        -String generation
        +getDescription() String
    }

    class Accessory {
        <<abstract>>
        #List~String~ compatibleConsoles
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

    Product <|-- VideoGame : extends
    Product <|-- Console : extends
    Product <|-- Accessory : extends
    Accessory <|-- Controller : extends
    Accessory <|-- Cable : extends
    Accessory <|-- Memory : extends

    %% Warranty Hierarchy
    class Warranty {
        <<abstract>>
    }
    class BasicWarranty
    class ExtendedWarranty

    Warranty <|-- BasicWarranty : extends
    Warranty <|-- ExtendedWarranty : extends
```
