## **Guiding questions for the analysis**

### Regarding the people in the system

 1. ***What attributes are common to all people interacting with the store, and which are specific to each role? How is this distinction reflected in a class hierarchy?***

* **Common Attributes:** Name (`name`), Identification Document (`id`), and Contact Phone Number (`phnNumber`)
* **Specific Attributes:**
   * **Customer (`Customer`):** Email address (`email`) and purchase history (`prchHistory`).
   * **Seller (`Seller`):** Employee code (`employeeId`) and work shift (`wrkShift`)
* **Class Hierarchy Distinction:** Common attributes are grouped in an abstract base class called `Person`. Specific attributes are declared in the derived concrete classes (`Customer` and `Seller`), which extend `Person` using inheritance

 2. ***Should there be a class representing a "generic person" without specifying a role? Why or why not? What implication does this decision have on instantiation?***

* **Decision:** Yes, a base class representing a generic person should exist to group common attributes, promote code reuse, and allow future flexibility when creating new types of people in the system. However, it should not represent a concrete entity on its own
* **Justification:** In the system context, every person must have a specific role (either a Customer or a Seller). Creating a generic person without a defined role does not make sense for the business logic
* **Implication on Instantiation:** Because of this decision, the `Person` class must be declared as `abstract`. This prevents direct instantiation (e.g., `new Person()`), requiring the system to instantiate only concrete subclasses (`Customer` or `Seller`)

### Regarding the system products


 3. ***What characteristics are shared by all products sold in the store, regardless of their type? What characteristics are specific to each product type?***

* **Common Characteristics:** Unique identifier (`id`), title (`title`), price (`price`), and available stock quantity (`stockQuantity`). These belong to the abstract base class `Product`
* **Specific Characteristics:**
   * **Video Games (`VideoGame`):** Platform (`platform`), genre (`genre`), and recommended age rating (`ageRating`)
   * **Consoles (`Console`):** Brand (`brand`), model (`model`), and generation (`generation`)
 4. ***Each product type must present a complete description integrating its particular characteristics. How should this behavior be declared in the base class to guarantee custom implementation? What OOP mechanism enables this?***

* **Declaration in Base Class:** The base class `Product` must declare an abstract method named `public abstract String getDescription()`. Since it is abstract and has nobody in the base class, Java enforces that every concrete subclass (`VideoGame` and `Console`) provides its own specific implementation using the `@Override` annotation
* **OOP Mechanism:** **Polymorphism** (specifically method overriding via **Abstraction**). This allows the system to treat all products uniformly while executing the specific description behavior defined by each subclass

### On sales and relationships between entities

 5. ***A sale involves a customer, a seller, and one or more products. What types of relationships exist between the Sale class and the other classes in the system? Are these inheritance, association, composition, or another type? Justify***

* **Sale to Customer:** **Association** (or Aggregation). The `Sale` class holds a reference to the `Customer` who made the purchase, but the customer exists independently of any individual sale
* **Sale to Seller:** **Association** (or Aggregation). The `Sale` class references the `Seller` who processed the transaction, but the seller exists independently in the system
* **Sale to Product:** **Aggregation** (represented via a collection like `List<Product>`). A sale groups multiple products together, but deleting or registering a sale does not destroy the products themselves from the store's inventory
* **Justification:** Inheritance does not apply because a Sale is not a type of Person or Product. Composition does not apply because destroying a Sale record should not delete the Customer, Seller, or Products from the database, as they have independent lifecycles
 6. ***Should the sale be responsible for calculating its own total, or should this responsibility belong to another class? Argument your decision***

* **Decision:** Yes, the `Sale` class should be responsible for calculating its own total by implementing a method (e.g., `calculateTotal()`)
* **Justification:** Following object-oriented design principles (specifically the Information Expert pattern), the class that holds the required data should perform the operations on it. Since `Sale` contains the collection of purchased products along with their prices and quantities, it has all the necessary information to compute the total directly, avoiding unnecessary coupling with external classes

### Regarding business restrictions

 7. ***How is it guaranteed in the design that a sale cannot be registered without at least one product? At what point in the system should this rule be validated?***

* **Design Guarantee:** The system checks that the collection of products associated with the sale is not null and contains at least one item (`!products.isEmpty()`)
* **Validation Point:** This rule must be validated primary in the **Service Layer (`SaleService`)** before initiating the transaction processing and data persistence. Additionally, the `Sale` class constructor can perform a defense check throwing an exception if initialized with an empty list

8. ***How is the automatic inventory update reflected in the design when a sale is registered? Which classes are involved in this operation?***

* **Operation Workflow:**
  1. When a sale is being processed, the `SaleService` iterates through the list of requested products
  2. It verifies that the required quantity does not exceed the available stock (`stockQuantity`). If stock is insufficient, the sale is rejected.
  3. If stock is available, `SaleService` updates each product's stock by calling its setter method (e.g., `product.setStockQuantity(current - sold)`)
  4. Finally, `SaleService` interacts with `ProductService` / `ProductRepository` to save the updated inventory levels back to the storage files
* **Involved Classes:** `SaleService`, `ProductService`, `ProductRepository`, `Sale`, and `Product` (along with its concrete subclasses `VideoGame` and `Console`)

### On layered organization

 9. ***The system must be organized into four layers: model, persistence, services, and user interface. What type of classes belong to each layer? What criterion determines in which layer a class should be located?***

* **Classes per Layer:**
  * **Model (`model`):** Domain entity classes representing real-world business objects (`Person`, `Customer`, `Seller`, `Product`, `VideoGame`, `Console`, `Sale`)
  * **Persistence (`persistence`):** Data access classes responsible for loading, reading, and saving data to storage files (`ProductRepository`, `PersonRepository`, `SaleRepository`)
  * **Services (`service`):** Business logic classes that enforce rules, validate constraints, and orchestrate workflows (`ProductService`, `PersonService`, `SaleService`)
  * **User Interface (`ui`):** Presentation classes managing console interaction, menu navigation, input collection, and displaying outputs (`ConsoleMenu`)
* **Classification Criterion:** Classes are placed based on the **Separation of Concerns (SoC)** principle. A class is assigned to a layer according to its primary responsibility: state representation (`model`), file I/O (`persistence`), business validation/rules (`service`), or user interaction (`ui`)

10. ***Why shouldn't the logic for saving and retrieving data from files be placed inside domain model classes? What problems are created when these responsibilities are mixed?***

* **Reasoning:** Domain classes (like `Product`, `Person`, or `Sale`) should focus exclusively on representing business entities, their attributes, and their core behaviors. Storage and file I/O operations are technical details independent of domain concepts
* **Problems Created by Mixing Responsibilities:**
  * **Violation of Single Responsibility Principle (SRP):** Classes would have multiple reasons to change (changes in business logic vs. changes in file formats)
  * **High Coupling:** Changing the persistence strategy (e.g., switching from plain text files to a database) would force modifying core domain entities
  * **Reduced Maintainability and Reusability:** The code becomes harder to read, debug, test, and reuse in different contexts

 11. ***What dependencies are allowed between layers and which are forbidden? Justify the direction of the allowed dependencies.***

* **Allowed Dependencies:**
  * `ui` $\rightarrow$ `service`
  * `service` $\rightarrow$ `persistence`
  * `service` $\rightarrow$ `model`
  * `persistence` $\rightarrow$ `model`
  * `model` $\rightarrow$ None (Independent)
* **Forbidden Dependencies:**
  * Direct access from `ui` to `persistence` (bypassing business rule validations in the service layer)
  * Domain `model` referencing higher layers (`persistence`, `service`, or `ui`)
  * Any circular or upward dependencies (e.g., `persistence` calling `ui`)
* **Justification for Allowed Direction:** This unidirectional dependency flow ensures that core business logic (`model`) remains completely independent and isolated from technical implementation details (like file formats or UI type). Lower layers do not know about higher layers, allowing the UI or persistence storage to be replaced or modified without altering core domain entities