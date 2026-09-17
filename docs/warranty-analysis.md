## **Analysis and Design of Change**

### **Warranty**
1.  ***The two types of warranty share common attributes (dates, associated product) but also have distinct attributes and behaviors (duration, coverage, cost). How is this situation reflected in the design of the class hierarchy? Which object-oriented programming mechanism allows each warranty type to have its own duration without duplicating code?***

* **A new abstract class will be created in the model layer, from which the two warranty specifications `basic` and `extended` will inherit.**
* **Inheritance—this is how two child classes of the same type are created.**

2. ***The business rule states that only consoles come with an automatic basic warranty, not video games. At which system layer is this decision located, and what Java mechanism is used to verify the actual type of a product? Justify your answer.***

* **It is implemented in the service layer, performing checks using exceptions.**

3. ***The duration of each type of warranty varies (6 months or 12 months). How
   is the expiration date calculated for each subclass? Should this calculation
   be performed in the warranty constructor or in a separate method? Justify your answer.***

   * **A public method is used in the abstract class to detect the current date and generate an end date; a boolean method then verifies this date and returns true or false, depending on whether or not it falls within the permitted timeframe.**
4. ***The extended warranty adds a cost equal to 10% of the product price to the total sale amount. At what point in the sale registration flow is this additional cost calculated and applied? What modifications are required for the `SaleService.registerSale` method?***

* **The additional cost is calculated within the extended warranty subclass; to apply it, a modification is required in the service layer to capture the 10% surcharge and add it to the total.**

5. ***The query for "guarantees nearing expiration" requires iterating over all guarantees and filtering for those with an end date within the next 30 days. In which class is this method located, and what dependencies does it require? Why is this placement consistent with the layered architecture?***

* **It should be placed in the `WarrantyService` class within the service layer, as this component is responsible for filtering and processing the information. It requires only the start dates and the local date to analyze warranties with end dates falling within the next 30 local days; consequently, it needs access to the warranty repository—that is, the persistence layer.**