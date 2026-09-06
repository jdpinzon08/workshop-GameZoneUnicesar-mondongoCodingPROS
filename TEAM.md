# Team Members & Task Distribution

## General Project Information
* **Project Name:** GameZoneUnicesar
* **Repository:** [https://github.com/TU_USUARIO/GameZoneUnicesar](https://github.com/TU_USUARIO/GameZoneUnicesar)
* **Architecture:** 4-Layer Separation (Model, Persistence, Service, UI)

---

## Team Members

### 1. Lead Technical & Integrator
* **Name:** Jose Daniel Pinzon Racero
* **ID:** 1121531780
* **Institutional Email:** jdpinzon@unicesar.edu.co
* **GitHub Username:** jdpinzon08
* **Assigned Role:** Technical Lead, Integration & Sales Module Manager

**Assigned Classes (5):**
1. `Sale` (Domain Model Class)
2. `SaleRepository` (Persistence Layer Class)
3. `SaleService` (Service Layer Class)
4. `ConsoleMenu` (User Interface Class)
5. `Main` / `App` (Application Entry Point Class)

---

### 2. Developer 1 (Product Module)
* **Name:** Geovanny Santiago Pino Avendaño
* **ID:** 1066872835
* **Institutional Email:** gspino@unicesar.edu.co
* **GitHub Username:** GeovannyPx
* **Assigned Role:** Developer 1 — Product Module

**Assigned Classes (5):**
1. `Product` (Abstract Domain Model Class)
2. `VideoGame` (Concrete Domain Model Class)
3. `Console` (Concrete Domain Model Class)
4. `ProductRepository` (Persistence Layer Class)
5. `ProductService` (Service Layer Class)

---

### 3. Developer 2 (Person Module)
* **Full Name:** Juan Diego Pabón Ortiz
* **ID / Identification:** 1065584421
* **Institutional Email:** jdiegopabon@unicesar.edu.co
* **GitHub Username:**
  JuanDiegoPabon
* **Assigned Role:** Developer 2 — Person Module

**Assigned Classes (5):**
1. `Person` (Abstract Domain Model Class)
2. `Customer` (Concrete Domain Model Class)
3. `Seller` (Concrete Domain Model Class)
4. `PersonRepository` (Persistence Layer Class)
5. `PersonService` (Service Layer Class)

---

## Workflow & Git Guidelines
* **Integration Branch:** `develop`
* **Feature Branches:** `feature/product-module`, `feature/person-module`, `feature/sale-module`
* **Commit Standard:** Conventional Commits in English (e.g., `feat(model): add Person domain class`)
* **Review Process:** All code integrations require a Pull Request (PR) approved by the Technical Lead.