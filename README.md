# workshop-GameZoneUnicesar-mondongoCodingPROS
Second workshop for the Programming 3 course: simulating a video game store in Valledupar.

# GameZone Unicesar - Core Management System

Sistema integral de gestión comercial, inventario, ventas, promociones, garantías y devoluciones desarrollado para la tienda de videojuegos y tecnología GameZone Unicesar. El proyecto está implementado en Java bajo una arquitectura por capas estricta, aplicando principios de diseño orientado a objetos (SOLID), patrones GRASP y persistencia ligera basada en archivos planos CSV.

---

## 1. Arquitectura del Sistema

El sistema implementa una arquitectura desacoplada en cuatro capas principales y dos capas transversales de soporte, respetando estrictamente el sentido unidireccional de dependencias (`ui` → `service` → `persistence` → `model`):

com.gamezone

├── exceptions     <-- [Capa Transversal] Jerarquía tipada de errores no comprobados

├── validation     <-- [Capa Transversal] Reglas y restricciones de dominio sin estado

├── model          <-- [Capa de Dominio] Entidades, jerarquías de productos y reglas de negocio

├── persistence    <-- [Capa de Datos] Repositorios con lectura/escritura en archivos CSV

├── service        <-- [Capa de Lógica] Orquestación de casos de uso y transacciones

└── ui             <-- [Capa de Presentación] Interfaz de línea de comandos en consola

* **Capa de Presentación (`com.gamezone.ui`):** Maneja la interacción con el usuario en consola, captura entradas, formatea salidas y atrapa excepciones diferenciadas sin exponer detalles técnicos.
* **Capa de Lógica de Negocio (`com.gamezone.service`):** Orquesta los casos de uso, coordina transacciones, delega comprobaciones a la capa de validación y administra los repositorios.
* **Capa de Acceso a Datos (`com.gamezone.persistence`):** Encapsula el acceso y formateo de archivos planos CSV, traduciendo fallos de I/O en excepciones de persistencia.
* **Capa de Dominio (`com.gamezone.model`):** Contiene las entidades puras del negocio con encapsulamiento estricto y cálculo polimórfico.
* **Capa de Validadores (`com.gamezone.validation`):** Clases utilitarias con métodos estáticos para verificar invariantes de negocio antes de la ejecución de servicios.
* **Capa de Excepciones (`com.gamezone.exceptions`):** Jerarquía de excepciones personalizadas derivadas de `RuntimeException` para comunicación semántica de fallos.

---

## 2. Módulos y Funcionalidades Implementadas

### Módulo de Catálogo y Productos (Herencia y Polimorfismo)
Gestiona el inventario de la tienda mediante la clase abstracta `Product`:
* **Videojuegos (`VideoGame`):** Plataforma, género y clasificación por edad.
* **Consolas (`Console`):** Marca, modelo y generación.
* **Accesorios (`Accessory`):** Extiende de `Product` y modela compatibilidad con consolas:
    * *Controles (`Controller`):* Tipo de conexión (alámbrico/inalámbrico).
    * *Cables (`Cable`):* Longitud en metros y tipo de conector (HDMI, USB, óptico, etc.).
    * *Memorias (`Memory`):* Capacidad en GB y tipo (SD, MicroSD, interna).

### Módulo de Ventas y Personas
Coordina la interacción comercial del establecimiento:
* **Personas (`Person`):** Clase abstracta base extendida por `Customer` (clientes) y `Seller` (vendedores).
* **Venta (`Sale`):** Agrupa productos vendidos, cliente asociado, vendedor responsable, fecha de emisión, cálculo de total, desglose de promociones aplicadas y verificación de plazos.

### Módulo de Promociones y Descuentos
Aplica estrategias de descuento automático seleccionando la opción más beneficiosa para el cliente (`findBestPromotionFor`):
* **Porcentaje General (`PercentageDiscount`):** Descuento directo sobre el total de la transacción.
* **Por Categoría (`CategoryDiscount`):** Descuento aplicado únicamente a los ítems pertenecientes a una categoría específica (`CONSOLE`, `VIDEOGAME`, etc.).
* **Por Volumen de Compra (`BulkPurchaseDiscount`):** Descuento porcentual condicionado a un número mínimo de unidades[cite: 23, 26, 27].

### Módulo de Garantías
Formaliza la cobertura técnica de productos vendidos:
* **Garantía Básica (`BasicWarranty`):** Asignada automáticamente a toda venta de consolas, vigencia de 6 meses sin costo extra.
* **Garantía Extendida (`ExtendedWarranty`):** Opcional a solicitud del cliente al comprar consolas, vigencia de 12 meses con costo adicional del 10% sobre el valor del equipo.
* Filtro y consulta de garantías activas y próximas a expirar en un rango de días determinado.

### Módulo de Devoluciones y Balance Mensual
Control formal de devoluciones y conciliación financiera:
* **Registro de Devolución (`Return`):** Referencia a la venta original, motivo, fecha de registro y lista parcial o total de productos devueltos.
* **Reglas de Negocio:** Validación de pertenencia del producto a la venta original y límite estricto de 30 días posteriores a la transacción (`canBeReturned`).
* **Restauración de Stock:** Reintegro automático de existencias en el inventario al procesar devoluciones conformes.
* **Balance Mensual:** Cálculo financiero del mes (`Ventas Totales - Devoluciones Totales`).

### Módulo de Validaciones y Excepciones Tipadas
Capa transversal de control de fallos y validación preventiva:
* **Jerarquía de Excepciones:**
    * `GameZoneException`: Raíz abstracta basada en `RuntimeException` con códigos de error.
    * `ResourceNotFoundException`: Lanzada cuando un ID no existe en el sistema.
    * `BusinessRuleException`: Reglas de negocio incumplidas (stock insuficiente, plazos vencidos, registros duplicados).
    * `InvalidDataException`: Datos de entrada nulos, formatos inválidos o rangos numéricos incorrectos.
    * `PersistenceException`: Envuelve excepciones de I/O preservando la causa técnica sin exponerla al usuario.
* **Validadores:** `ProductValidator`, `PersonValidator` y `SaleValidator` desacoplan las comprobaciones lógicas de los servicios.

---

## 3. Principios de Diseño y Patrones Aplicados

* **SRP (Single Responsibility Principle):** Cada clase tiene un único propósito delimitado; las validaciones residen en validadores dedicados, la persistencia en repositorios y el dominio permanece libre de llamadas a disco.
* **OCP (Open/Closed Principle):** La jerarquía de productos, garantías y promociones permite agregar nuevas variantes mediante herencia y polimorfismo sin modificar los servicios existentes.
* **LSP (Liskov Substitution Principle):** Las subclases respetan el comportamiento del contrato base sin alterar precondiciones ni lanzar excepciones no soportadas.
* **ISP (Interface Segregation Principle):** Interfaces y repositorios segregados para no forzar implementaciones innecesarias.
* **DIP (Dependency Inversion Principle):** Los servicios reciben sus dependencias por inyección en el constructor, desacoplándose de la instanciación interna.
* **Patrones GRASP:**
    * *Information Expert:* Los cálculos residen en las clases que poseen la información (`Sale`, `Return`, `Promotion`).
    * *High Cohesion & Low Coupling:* Mantenido a lo largo del flujo en capas.
    * *Protected Variations:* La jerarquía de excepciones y modelos protege al sistema de cambios internos en validaciones o formatos de datos.

---

## 4. Estructura de Persistencia (Archivos CSV)

El sistema almacena el estado operativo en archivos de texto plano dentro del directorio `data/`:

* `data/product.csv`: Videojuegos, consolas y accesorios diferenciados por columna discriminadora (`videoGame`, `console`, `controller`, `cable`, `memory`).
* `data/persons.csv`: Registros de clientes (`Customer`) y empleados (`Seller`) con roles y contactos.
* `data/sales.csv`: Transacciones con formato `saleId,date,customerId,sellerId,productIds,totalAmount,appliedPromo,discountAmount`.
* `data/warranties.csv`: Garantías emitidas vinculando ID de producto, ID de venta, vigencia y tipo de cobertura.
* `data/returns.csv`: Historial de devoluciones vinculadas a ventas específicas con montos reintegrados.
* `data/promotions.csv`: Parámetros, fechas de vigencia y tipos de descuentos vigentes.

---

## 5. Requisitos y Ejecución

### Requisitos Previos
* **Java Development Kit (JDK):** Versión 17 o superior.
* **Apache Maven:** Versión 3.8 o superior.
* **Git:** Para control de versiones y flujo de trabajo.

### Compilación y Empaquetado
Para compilar el proyecto y verificar que no existan errores sintácticos ni fallas de pruebas:
```bash
mvn clean compile
```
Para generar el archivo ejecutable (JAR):

```bash
mvn clean package
```

Ejecución de la Aplicación
Para iniciar el sistema por consola interactiva:
```bash
java -cp target/gamezone-core-1.0-SNAPSHOT.jar com.gamezone.Main
```

## 6. Flujo de Trabajo y Control de Versiones
El equipo utiliza una adaptación rigurosa de Git Flow con restricciones académicas:

    Ramas Principales:
main: Código estable de producción. 

develop: Rama base de desarrollo e integración continua.


    Ramas de Funcionalidad:


Formato de rama: feature/<nombre-requerimiento>.   

Commits atómicos con mensajes en inglés bajo el estándar Conventional Commits (feat:, refactor:, docs:, fix:).

Integración exclusiva mediante Pull Requests (PRs) revisados y fusionados hacia develop.   