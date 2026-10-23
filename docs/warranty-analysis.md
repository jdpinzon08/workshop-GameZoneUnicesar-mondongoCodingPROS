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

5. ***The query for warranties nearing expiration filters warranties whose end date falls within a requested number of days. In which class is this method located, and what dependencies does it require? Why is this placement consistent with the layered architecture?***

* **It is implemented in `WarrantyService`, which filters the loaded warranties by `endDate` and the current date. `WarrantyService` uses `WarrantyRepository` to load and persist warranties.**
