# workshop-GameZoneUnicesar-mondongoCodingPROS
Second workshop for the Programming 3 course: simulating a video game store in Valledupar.

# GameZone Unicesar - Core Management System

A comprehensive management system for commerce, inventory, sales, promotions, warranties, and returns, developed for the video game and technology store "GameZone Unicesar." The project is implemented in Java using a strict layered architecture, applying object-oriented design principles (SOLID), GRASP patterns, and lightweight persistence based on flat CSV files.

---

## 1. System Architecture

The system implements a decoupled architecture consisting of four primary layers and two cross-cutting support layers, strictly adhering to a unidirectional dependency flow (`ui` → `service` → `persistence` → `model`):

com.gamezone

├── exceptions     <-- [Cross-cutting Layer] Typed hierarchy of unchecked exceptions

├── validation     <-- [Cross-cutting Layer] Stateless domain rules and constraints

├── model          <-- [Domain Layer] Entities, product hierarchies, and business rules

├── persistence    <-- [Data Layer] Repositories for reading/writing to CSV files

├── service        <-- [Logic Layer] Orchestration of use cases and transactions

└── ui             <-- [Presentation Layer] Console-based command-line interface

* **Presentation Layer (`com.gamezone.ui`):** Handles user interaction via the console, captures input, formats output, and catches specific exceptions without exposing technical details.
* **Business Logic Layer (`com.gamezone.service`):** Orchestrates use cases, coordinates transactions, delegates checks to the validation layer, and manages the repositories. * **Data Access Layer (`com.gamezone.persistence`):** Encapsulates access to and formatting of CSV flat files, translating I/O failures into persistence exceptions.
* **Domain Layer (`com.gamezone.model`):** Contains pure business entities with strict encapsulation and polymorphic logic.
* **Validator Layer (`com.gamezone.validation`):** Utility classes with static methods for verifying business invariants prior to service execution.
* **Exception Layer (`com.gamezone.exceptions`):** A hierarchy of custom exceptions derived from `RuntimeException`, designed for the semantic communication of failures.

---

## 2. Implemented Modules and Functionalities

### Catalog and Products Module (Inheritance and Polymorphism)
Manages the store's inventory using the abstract class `Product`:
* **Video Games (`VideoGame`):** Platform, genre, and age rating.
* **Consoles (`Console`):** Brand, model, and generation.
* **Accessories (`Accessory`):** Extends `Product` and models compatibility with consoles:
  * *Controllers (`Controller`):* Connection type (wired/wireless).
  * *Cables (`Cable`):* Length in meters and connector type (HDMI, USB, optical, etc.).
  * *Memory/Storage (`Memory`):* Capacity in GB and type (SD, MicroSD, internal).

### Sales and Personnel Module
Coordinates the establishment's commercial interactions:
* **People (`Person`):** An abstract base class extended by `Customer` and `Seller`.
* **Sale (`Sale`):** Groups together the products sold, the associated customer, the responsible seller, the date of issuance, the total calculation, a breakdown of applied promotions, and verification of relevant timeframes.
### Promotions and Discounts Module
Apply automated discount strategies by selecting the option most beneficial to the customer (`findBestPromotionFor`):
* **General Percentage Discount (`PercentageDiscount`):** A direct discount applied to the total transaction amount.
* **Category-Based Discount (`CategoryDiscount`):** A discount applied exclusively to items belonging to a specific category (e.g., `CONSOLE`, `VIDEOGAME`).
* **Bulk Purchase Discount (`BulkPurchaseDiscount`):** A percentage-based discount contingent upon a minimum number of units purchased.

### Warranty Module
Manage technical coverage for sold products:
* **Basic Warranty (`BasicWarranty`):** Automatically assigned to all console sales; valid for 6 months at no extra cost.
* **Extended Warranty (`ExtendedWarranty`):** Optional, available upon customer request when purchasing consoles; valid for 12 months for an additional fee equal to 10% of the device's value.
* Includes filtering and lookup capabilities for active warranties and those nearing expiration within a specified timeframe.

### Returns and Monthly Balance Module
Formal control of returns and financial reconciliation:
* **Return Record (`Return`):** Includes a reference to the original sale, the reason, the date of registration, and a list of the products returned (partial or total).
* **Business Rules:** Validation that the product belongs to the original sale, with a strict 30-day limit following the transaction (`canBeReturned`).
* **Stock Restoration:** Automatic replenishment of inventory levels upon processing valid returns.
* **Monthly Balance:** Financial calculation for the month (`Total Sales - Total Returns`).

### Validations and Typed Exceptions Module
Cross-cutting layer for error handling and proactive validation:
* **Exception Hierarchy:**
* `GameZoneException`: An abstract base class extending `RuntimeException`, incorporating error codes.
* `ResourceNotFoundException`: Thrown when an ID does not exist in the system.
* `BusinessRuleException`: Triggered when business rules are violated (e.g., insufficient stock, expired timeframes, duplicate records).
* `InvalidDataException`: Used for null input data, invalid formats, or incorrect numerical ranges.
* `PersistenceException`: Wraps I/O-related exceptions, preserving the underlying technical cause without exposing it to the end user.
* **Validators:** `ProductValidator`, `PersonValidator`, and `SaleValidator` decouple logical checks from the service layer.

---

## 3. Design Principles and Applied Patterns

* **SRP (Single Responsibility Principle):** Each class has a single, well-defined purpose; validations reside in dedicated validator classes, persistence is handled by repositories, and the domain layer remains free of disk I/O operations.
* **OCP (Open/Closed Principle):** The hierarchy of products, warranties, and promotions allows for the addition of new variants through inheritance and polymorphism, without requiring modifications to existing services.
* **LSP (Liskov Substitution Principle):** Subclasses adhere to the behavior defined by the base contract, without altering preconditions or throwing unsupported exceptions.
* **ISP (Interface Segregation Principle):** Interfaces and repositories are segregated to avoid forcing classes to implement unnecessary methods.
* **DIP (Dependency Inversion Principle):** Services receive their dependencies via constructor injection, decoupling them from internal instantiation logic.
* **GRASP Patterns:**
* *Information Expert:* Calculations are placed within the classes that possess the necessary information (`Sale`, `Return`, `Promotion`).
* *High Cohesion & Low Coupling:* Maintained throughout the layered architecture.
* *Protected Variations:* The hierarchy of exceptions and models shields the system from internal changes, such as modifications to validation logic or data formats.

---
## 4. Persistence Structure (CSV Files)

The system stores its operational state in plain-text files within the `data/` directory:

* `data/product.csv`: Video games, consoles, and accessories, distinguished by a category column (`videoGame`, `console`, `controller`, `cable`, `memory`).
* `data/persons.csv`: Records of customers (`Customer`) and employees (`Seller`), including their roles and contact information.
* `data/sales.csv`: Transactions, formatted as `saleId,date,customerId,sellerId,productIds,totalAmount,appliedPromo,discountAmount`.
* `data/warranties.csv`: Issued warranties, linking product IDs, sale IDs, validity periods, and types of coverage.
* `data/returns.csv`: A history of returns linked to specific sales, including the amounts refunded.
* `data/promotions.csv`: Parameters, validity dates, and types of active discounts.

---

## 5. Requirements and Execution

### Prerequisites
* **Java Development Kit (JDK):** Version 17 or higher.
* **Apache Maven:** Version 3.8 or higher.
* **Git:** For version control and workflow.

### Compilation and Packaging
To compile the project and verify that there are no syntax errors or test failures:
```bash
mvn clean compile
```
To generate the executable file (JAR):

```bash
mvn clean package
```

Running the Application
To start the system via an interactive console:
```bash
java -cp target/gamezone-core-1.0-SNAPSHOT.jar com.gamezone.Main
```

## 6. Workflow and Version Control
The team uses a rigorous adaptation of Git Flow with academic constraints:

Main Branches:
main: Stable production code.

develop: Base branch for development and continuous integration.


Feature Branches:


Branch naming format: feature/<requirement-name>.

Atomic commits with English-language messages following the Conventional Commits standard (feat:, refactor:, docs:, fix:).

Integration exclusively via Pull Requests (PRs) that are reviewed and merged into the develop branch.  